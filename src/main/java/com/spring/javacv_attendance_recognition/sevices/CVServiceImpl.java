package com.spring.javacv_attendance_recognition.sevices;

import jakarta.annotation.PostConstruct;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
public class CVServiceImpl implements CVService{

    @Value("${pathDirectory.srcTrainDirectory}")
    private String srcTrainDirectory;

    @Value("${pathDirectory.destinationTrainDirectory}")
    private String destinationTrainDirectory;

    private final CascadeClassifier cascadeClassifier =
            new CascadeClassifier("src/main/resources/static/haarcascade_frontalface_alt2.xml");

    LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();

    @Override
    public String trainModel() {
        //Read files image from directory
        File[] srcFiles =  readDirectory(srcTrainDirectory);
        if(srcFiles == null) return "Not found Image to train!";
        MatVector matVectorImages = new MatVector(srcFiles.length);

        // Label of trainFile
        Mat label = new Mat(srcFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuffer = label.createBuffer();

        int counter = 0;
        for (File fileImg: srcFiles) {
            // Get image from src, changed color and size of image
            Mat matImg = imread(fileImg.getAbsolutePath(), COLOR_BGRA2GRAY);
            RectVector rectVector = new RectVector();
            cascadeClassifier.detectMultiScale(matImg, rectVector);
            for(Rect rect: rectVector.get()){
                // Detect face and draw rectangle
                rectangle(matImg, rect, new Scalar(255, 255, 0, 3), 1, 0, 0);
                matImg = new Mat(matImg, rect);
                resize(matImg, matImg, new Size(160, 160));
                cvtColor(matImg, matImg, COLOR_BGRA2GRAY);
            }

            // Get id of person to labeling vector
            int personId = Integer.parseInt(fileImg.getName().split("_")[0]);

            // Put vector to mat
            matVectorImages.put(counter, matImg);
            labelsBuffer.put(counter, personId);
            counter++;
        }
        recognizer.train(matVectorImages, label);
        recognizer.save(destinationTrainDirectory);
        return "successful";
    }

    @Override
    public String recognize() {

        // image Test recognize
        File srcImg = new File("src/main/resources/static/train/source/002_vvhoang.png");

        RectVector rectVectorDetectedFace = new RectVector();
        // Read trainFile
        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.read(destinationTrainDirectory);
        recognizer.setThreshold(50);

        // ImageGray to recognized
        Mat matImg = imread(srcImg.getAbsolutePath());
        cvtColor(matImg, matImg, COLOR_BGRA2GRAY);

        cascadeClassifier.detectMultiScale(matImg, rectVectorDetectedFace);
        int prediction = 0;
        double result = 0;
        for (Rect rect: rectVectorDetectedFace.get()){
            // Detected face
            rectangle(matImg, rect, new Scalar(0, 255, 0, 3), 1,0,0);
            Mat faceToRecognized = new Mat(matImg, rect);
            resize(faceToRecognized, faceToRecognized, new Size(160, 160));

            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            recognizer.predict(faceToRecognized, label, confidence);

            prediction = label.get(0);
            result = confidence.get(0);
            System.out.println(prediction == -1);
        }
        return prediction + " " +  result;
    }

    public File[] readDirectory(String path){

        //mention the directory the faces has been saved
        File directory = new File(path);
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg")
                        || name.endsWith(".pgm")
                        || name.endsWith(".png");
            }
        };
        return directory.listFiles(filenameFilter);
    }
}
