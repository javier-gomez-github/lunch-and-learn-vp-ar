package net.velocitypartners.lunchandlearn.ar.util;

import jp.nyatla.nyar4psg.MultiMarker;
import jp.nyatla.nyar4psg.NyAR4PsgConfig;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

import java.io.File;
import java.io.FilenameFilter;

public class ARHelper extends PApplet {

    // the full path to the camera_para.dat file
    protected final String camPara = "extra/camera_para.dat";
    // the full path to the .patt pattern files
    protected final String patternPath = "extra/patterns";
    // the dimensions at which the AR will take place. with the current library 1280x720 is about the highest possible resolution.
    protected final int arWidth = 1440;
    protected final int arHeight = 900;
    // the number of pattern markers (from the complete list of .patt files) that will be detected, here the first 100 from the list.
    protected final int numMarkers = 100;
    protected float displayScale;
    protected final int[] colors = new int[numMarkers];

    protected Capture camera;
    protected MultiMarker nya;

    public void setup()
    {
        // initialize Camera
        camera = new Capture(this, "name=HD Pro Webcam C920,size=1440x900,fps=30");

        // resize the sketch and set the renderer
        size(arWidth, arHeight, OPENGL);

        // start capturing
        camera.start();

        // corrects the scale difference between the AR detection coordinates and the size at which the result is displayed
        displayScale = (float) width / arWidth;

        // turn off stroke for the rest of this sketch
        noStroke();

        // create a new MultiMarker at a specific resolution (arWidth x arHeight), with the default camera calibration and coordinate system
        nya = new MultiMarker(this, arWidth, arHeight, camPara, NyAR4PsgConfig.CONFIG_DEFAULT);

        // set the delay (0 - 255) after which a lost marker is no longer displayed. by default set to something higher, but here manually set to immediate.
        nya.setLostDelay(1);

        // load all available Patterns in the path provided
        String[] patterns = loadPatternFileNames(patternPath);
        // for the selected number of markers, add the marker for detection
        for (int i=0; i<numMarkers; i++) {
            nya.addARMarker(patternPath + "/" + patterns[i], 80);
        }
    }

    private String[] loadPatternFileNames(String path) {
        File folder = new File(path);
        FilenameFilter pattFilter = (dir, name) -> name.toLowerCase().endsWith(".patt");
        return folder.list(pattFilter);
    }

    protected void initCameraAndDetectMarkers() {
        // reads the image from the camera
        camera.read();
        // a background call is needed for correct display of the marker results
        background(camera);
        // display the image at the width and height of the sketch window
        image(camera, 0, 0, width, height);
        // create a copy of the camera image and resize it to the resolution of the AR detection (otherwise nya.detect will throw an assertion error)
        PImage cSmall = camera.get();
        cSmall.resize(arWidth, arHeight);
        // detect markers in the image
        nya.detect(cSmall);
    }
}
