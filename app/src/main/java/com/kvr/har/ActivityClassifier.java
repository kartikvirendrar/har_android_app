package com.kvr.har;

import android.content.Context;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class ActivityClassifier {

    private static final String MODEL_FILE="model.pb";
    private static final String INPUT_NODE="lstm_1_input";
    private static final String[] OUTPUT_NODES={"dense2/Softmax"};
    private static final String OUTPUT_NODE="dense2/Softmax";
    private static final long[] INPUT_SIZE={1,100,6};
    private static final int OUTPUT_SIZE = 3;

    static {
        System.loadLibrary("tensorflow_inference");
    }

    private final TensorFlowInferenceInterface inferenceInterface;

    public ActivityClassifier(Context context){
        inferenceInterface=new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
    }

    public float[] predictProbabilities(float[] data){
        float[] result=new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);
        return result;
    }

}
