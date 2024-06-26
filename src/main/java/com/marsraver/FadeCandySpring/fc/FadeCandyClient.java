package com.marsraver.FadeCandySpring.fc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;


public class FadeCandyClient {

    public static final int WIDTH = 512;
    public static final int HEIGHT = 256;
    public static float QUARTER_PI = (float) (Math.PI / 4.0f);
    public static Random RANDOM = new Random();
    private Socket socket;
    private OutputStream output;
    private String host;
    private int port;
    private int[] pixelLocations;
    private byte[] packetData;
    private byte firmwareConfig;
    private String colorCorrection;
    private boolean enableShowLocations;
    private OpcLayout layout;

    public FadeCandyClient(String host, int port, OpcLayout layout) {
        this.host = host;
        this.port = port;
        this.layout = layout;
        this.enableShowLocations = true;
    }

    public void ping() {
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            if (!inetAddress.isReachable(5000)) {
                System.err.println("IP address " + host + " is not reachable");
                System.exit(-1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int nextRandomInt() {
        return RANDOM.nextInt();
    }

    public int nextRandomInt(int size) {
        return RANDOM.nextInt(size);
    }

    // Set the location of a single LED
    public void led(int index, int x, int y) {
        // For convenience, automatically grow the pixelLocations array. We do
        // want this to be an array,
        // instead of a HashMap, to keep draw() as fast as it can be.
        if (pixelLocations == null) {
            pixelLocations = new int[index + 1];
        } else if (index >= pixelLocations.length) {
            pixelLocations = Arrays.copyOf(pixelLocations, index + 1);
        }

        pixelLocations[index] = x + layout.getWidth() * y;
    }

    public Pixel getPixel(int index) {
        if (pixelLocations == null || pixelLocations.length < index) {
            return null;
        }
        int y = pixelLocations[index] / layout.getWidth();
        int x = pixelLocations[index] - (y * layout.getWidth());

        return new Pixel(x, y);
    }

    // Set the location of several LEDs arranged in a strip.
// Angle is in radians, measured clockwise from +X.
// (x,y) is the center of the strip.
    void ledStrip(int index, int count, float x, float y, float spacing, float angle, boolean reversed) {
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);
        for (int i = 0; i < count; i++) {
            led(reversed ? (index + count - 1 - i) : (index + i),
                    (int) (x + (i - (count - 1) / 2.0) * spacing * c + 0.5),
                    (int) (y + (i - (count - 1) / 2.0) * spacing * s + 0.5));
        }
    }

    // Set the location of several LEDs arranged in a grid. The first strip is
// at 'angle', measured in radians clockwise from +X.
// (x,y) is the center of the grid.
    public void ledGrid(int index, int stripLength, int numStrips, float x, float y, float ledSpacing, float stripSpacing,
                        float angle, boolean zigzag) {
        float s = (float) Math.sin(angle + Math.PI / 2.0);
        float c = (float) Math.cos(angle + Math.PI / 2.0);
        for (int i = 0; i < numStrips; i++) {
            ledStrip(index + stripLength * i, stripLength, (float) (x + (i - (numStrips - 1) / 2.0) * stripSpacing * c),
                    (float) (y + (i - (numStrips - 1) / 2.0) * stripSpacing * s), ledSpacing, angle,
                    zigzag && (i % 2) == 1);
        }
    }

    // Set the location of 64 LEDs arranged in a uniform 8x8 grid.
// (x,y) is the center of the grid.
    public void ledGrid8x8(int index, float x, float y, float spacing, float angle, boolean zigzag) {
        ledGrid(index, 8, 8, x, y, spacing, spacing, angle, zigzag);
    }

    public void ledGrid16x16(int index, float x, float y, float spacing, float angle, boolean zigzag) {
        ledGrid(index, 16, 16, x, y, spacing, spacing, angle, zigzag);
    }

    // Should the pixel sampling locations be visible? This helps with
// debugging.
// Showing locations is enabled by default. You might need to disable it if
// our drawing
// is interfering with your processing sketch, or if you'd simply like the
// screen to be
// less cluttered.
    public void showLocations(boolean enabled) {
        enableShowLocations = enabled;
    }

    // Enable or disable dithering. Dithering avoids the "stair-stepping"
// artifact and increases color
// resolution by quickly jittering between adjacent 8-bit brightness levels
// about 400 times a second.
// Dithering is on by default.
    public void setDithering(boolean enabled) {
        if (enabled)
            firmwareConfig &= ~0x01;
        else
            firmwareConfig |= 0x01;
        sendFirmwareConfigPacket();
    }

    // Enable or disable frame interpolation. Interpolation automatically blends
// between consecutive frames
// in hardware, and it does so with 16-bit per channel resolution. Combined
// with dithering, this helps make
// fades very smooth. Interpolation is on by default.
    public void setInterpolation(boolean enabled) {
        if (enabled)
            firmwareConfig &= ~0x02;
        else
            firmwareConfig |= 0x02;
        sendFirmwareConfigPacket();
    }

    // Put the Fadecandy onboard LED under automatic control. It blinks any time
// the firmware processes a packet.
// This is the default configuration for the LED.
    public void statusLedAuto() {
        firmwareConfig &= 0x0C;
        sendFirmwareConfigPacket();
    }

    // Manually turn the Fadecandy onboard LED on or off. This disables
// automatic LED control.
    public void setStatusLed(boolean on) {
        firmwareConfig |= 0x04; // Manual LED control
        if (on)
            firmwareConfig |= 0x08;
        else
            firmwareConfig &= ~0x08;
        sendFirmwareConfigPacket();
    }

    // Set the color correction parameters
    public void setColorCorrection(float gamma, float red, float green, float blue) {
        colorCorrection = "{ \"gamma\": " + gamma + ", \"whitepoint\": [" + red + "," + green + "," + blue + "]}";
        sendColorCorrectionPacket();
    }

    // Set custom color correction parameters from a string
    public void setColorCorrection(String s) {
        colorCorrection = s;
        sendColorCorrectionPacket();
    }

    // Send a packet with the current firmware configuration settings
    public void sendFirmwareConfigPacket() {
        if (output == null) {
            // We'll do this when we reconnect
            return;
        }

        byte[] packet = new byte[9];
        packet[0] = 0; // Channel (reserved)
        packet[1] = (byte) 0xFF; // Command (System Exclusive)
        packet[2] = 0; // Length high byte
        packet[3] = 5; // Length low byte
        packet[4] = 0x00; // System ID high byte
        packet[5] = 0x01; // System ID low byte
        packet[6] = 0x00; // Command ID high byte
        packet[7] = 0x02; // Command ID low byte
        packet[8] = firmwareConfig;

        try {
            output.write(packet);
        } catch (Exception e) {
            dispose();
        }
    }

    // Send a packet with the current color correction settings
    public void sendColorCorrectionPacket() {
        if (colorCorrection == null) {
            // No color correction defined
            return;
        }
        if (output == null) {
            // We'll do this when we reconnect
            return;
        }

        byte[] content = colorCorrection.getBytes();
        int packetLen = content.length + 4;
        byte[] header = new byte[8];
        header[0] = 0; // Channel (reserved)
        header[1] = (byte) 0xFF; // Command (System Exclusive)
        header[2] = (byte) (packetLen >> 8);
        header[3] = (byte) (packetLen & 0xFF);
        header[4] = 0x00; // System ID high byte
        header[5] = 0x01; // System ID low byte
        header[6] = 0x00; // Command ID high byte
        header[7] = 0x01; // Command ID low byte

        try {
            output.write(header);
            output.write(content);
        } catch (Exception e) {
            dispose();
        }
    }

    // Automatically called at the end of each draw().
// This handles the automatic Pixel to LED mapping.
// If you aren't using that mapping, this function has no effect.
// In that case, you can call setPixelCount(), setPixel(), and writePixels()
// separately.
    public void draw() {
        layout.snap();
        if (pixelLocations == null || layout.getWritableImage() == null) {
            // No pixels defined yet
            return;
        }

        if (output == null) {
            // Try to (re)connect
            connect();
        }
        if (output == null) {
            return;
        }

        int numPixels = pixelLocations.length;
        int ledAddress = 4;

        setPixelCount(numPixels);
//        parent.loadPixels();

        for (int i = 0; i < numPixels; i++) {
            int pixel = layout.getOpcColor(getPixel(i)).getColorValue();

            packetData[ledAddress] = (byte) (pixel >> 16);
            packetData[ledAddress + 1] = (byte) (pixel >> 8);
            packetData[ledAddress + 2] = (byte) pixel;

            ledAddress += 3;

            if (enableShowLocations) {
                layout.setPixel(getPixel(i), 0xFFFFFF ^ pixel);
            }
        }
        writePixels();
//        if (enableShowLocations) {
//            parent.updatePixels();
//        }
    }


    public int getPixelCount() {
        return pixelLocations.length;
    }

    // Change the number of pixels in our output packet.
// This is normally not needed; the output packet is automatically sized
// by draw() and by setPixel().
    public void setPixelCount(int numPixels) {
        int numBytes = 3 * numPixels;
        int packetLen = 4 + numBytes;
        if (packetData == null || packetData.length != packetLen) {
            // Set up our packet buffer
            packetData = new byte[packetLen];
            packetData[0] = 0; // Channel
            packetData[1] = 0; // Command (Set pixel colors)
            packetData[2] = (byte) (numBytes >> 8);
            packetData[3] = (byte) (numBytes & 0xFF);
        }
    }

    // Directly manipulate a pixel in the output buffer. This isn't needed
// for pixels that are mapped to the screen.
    public void setPixel(int number, OpcColor c) {
        int offset = 4 + number * 3;
        if (packetData == null || packetData.length < offset + 3) {
            setPixelCount(number + 1);
        }

        packetData[offset] = (byte) c.getRed();
        packetData[offset + 1] = (byte) c.getGreen();
        packetData[offset + 2] = (byte) c.getBlue();
    }

    public void setPixel(int number, int color) {
        int offset = 4 + number * 3;
        if (packetData == null || packetData.length < offset + 3) {
            setPixelCount(number + 1);
        }

        packetData[offset] = (byte) (color >> 16);
        packetData[offset + 1] = (byte) (color >> 8);
        packetData[offset + 2] = (byte) color;
    }

// Read a pixel from the output buffer. If the pixel was mapped to the
// display,
// this returns the value we captured on the previous frame.
//    PColor getPixelColor(int number) {
//        int offset = 4 + number * 3;
//        if (packetData == null || packetData.length < offset + 3) {
//            return new PColor();
//        }
//        return new PColor((packetData[offset] << 16), (packetData[offset + 1] << 8), packetData[offset + 2]);
//    }

    // Transmit our current buffer of pixel values to the OPC server. This is
// handled
// automatically in draw() if any pixels are mapped to the screen, but if
// you haven't
// mapped any pixels to the screen you'll want to call this directly.
    void writePixels() {
        if (packetData == null || packetData.length == 0) {
            // No pixel buffer
            return;
        }
        if (output == null) {
            // Try to (re)connect
            connect();
        }
        if (output == null) {
            return;
        }

        try {
            output.write(packetData);
        } catch (Exception e) {
            dispose();
        }
    }

    public void dispose() {
        // Destroy the socket. Called internally when we've disconnected.
        if (output != null) {
            System.out.println("Disconnected from OPC server");
        }
        socket = null;
        output = null;
    }

    void connect() {
        // Try to connect to the OPC server. This normally happens automatically
        // in draw()
        try {
            socket = new Socket(host, port);
            socket.setTcpNoDelay(true);
            output = socket.getOutputStream();
            System.out.println("Connected to OPC server");
        } catch (IOException e) {
            dispose();
        }

        sendColorCorrectionPacket();
        sendFirmwareConfigPacket();
    }

    public void clear() {
        for (int i = 4; i < packetData.length; i++) {
            packetData[i] = 0;
        }
        writePixels();
    }


    public static enum Layout {
        ONE, TWO, FOUR, TOWER, TWO_DIAMOND, TRIPOD, EightByEight;
    }

}
