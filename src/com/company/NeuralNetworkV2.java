package com.company;

public class NeuralNetworkV2 {
    private int inputNodes;
    private int hiddenNodes1;
    private int hiddenNodes2;
    private int outputNodes;
    public double lr = 0.01;
    private Matrix weigts_ih1;
    private Matrix weigts_h1h2;
    private Matrix weigts_h2o;
    private Matrix bias_h1;
    private Matrix bias_h2;
    private Matrix bias_o;

    //todo
    // lav 2. hiddne layer

    public NeuralNetworkV2(int inputNodes, int hiddenNodes1, int hiddenNodes2, int outputNodes){
        this.inputNodes = inputNodes;
        this.hiddenNodes1 = hiddenNodes1;
        this.hiddenNodes2 = hiddenNodes2;
        this.outputNodes = outputNodes;
        this.weigts_ih1 = new Matrix(this.hiddenNodes1,this.inputNodes);
        this.weigts_h1h2 = new Matrix(this.hiddenNodes2, this.hiddenNodes1);
        this.weigts_h2o = new Matrix(this.outputNodes,this.hiddenNodes2);
        this.weigts_ih1.random();
        this.weigts_h1h2.random();
        this.weigts_h2o.random();
        this.bias_h1 = new Matrix(this.hiddenNodes1,1);
        this.bias_h2 = new Matrix(this.hiddenNodes2,1);
        this.bias_o = new Matrix(this.outputNodes,1);
        this.bias_h1.random();
        this.bias_h2.random();
        this.bias_o.random();


    }

    double [] guess (double[] inputArr){
        Matrix input = Matrix.arrToMatrix(inputArr);
        Matrix hidden1;
        hidden1 = Matrix.MatrixMultiply(this.weigts_ih1, input);
        hidden1.addMatrixSelv(this.bias_h1);
        hidden1 = Matrix.mapSigmoid(hidden1);
        // laver hidden 1 output
        Matrix hidden2;
        hidden2 = Matrix.MatrixMultiply(this.weigts_h1h2, hidden1);
        hidden2.addMatrixSelv(this.bias_h2);
        hidden2 = Matrix.mapSigmoid(hidden2);
        //laver  output
        Matrix output;
        output = Matrix.MatrixMultiply(this.weigts_h2o, hidden2);
        output.addMatrixSelv(this.bias_o);
        output = Matrix.mapSigmoid(output);
        double[] guess = Matrix.MatrixToArr(output);
        return guess;
    }

    void train (double[] inputArr, double[] answerArr){
        //definere input targe og output som Matrix
        Matrix input = Matrix.arrToMatrix(inputArr);
        Matrix target = Matrix.arrToMatrix(answerArr);

        Matrix hidden1;
        hidden1 = Matrix.MatrixMultiply(this.weigts_ih1, input);
        hidden1.addMatrixSelv(this.bias_h1);
        hidden1 = Matrix.mapSigmoid(hidden1);
        // laver hidden 1 output
        Matrix hidden2;
        hidden2 = Matrix.MatrixMultiply(this.weigts_h1h2, hidden1);
        hidden2.addMatrixSelv(this.bias_h2);
        hidden2 = Matrix.mapSigmoid(hidden2);
        //laver  output
        Matrix output;
        output = Matrix.MatrixMultiply(this.weigts_h2o, hidden2);
        output.addMatrixSelv(this.bias_o);
        output = Matrix.mapSigmoid(output);
        /*
        Matrix hidden;
        hidden = Matrix.MatrixMultiply(this.weigts_ih, input);
        hidden.addMatrixSelv(this.bias_h);
        //aktivatins funktion
        hidden = Matrix.mapSigmoid(hidden);
        // laver output
        Matrix output;
        output = Matrix.MatrixMultiply(this.weigts_ho, hidden);
        output.addMatrixSelv(this.bias_o);
        //aktivatins funktion
        output = Matrix.mapSigmoid(output);
        */

        Matrix outputError = Matrix.subtractElement(target, output);

        Matrix weights_h2o_t = Matrix.transpose(this.weigts_h2o);
        Matrix hidden2Error = Matrix.MatrixMultiply(weights_h2o_t, outputError);

        Matrix weights_h1h2_t = Matrix.transpose(this.weigts_h1h2);
        Matrix hidden1Error = Matrix.MatrixMultiply(weights_h1h2_t, hidden2Error);

        /*
        //finder output error
        Matrix outputError = Matrix.subtractElement(target, output);
        //finder hidden layer error
        Matrix weights_ho_t = Matrix.transpose(this.weigts_ho);
        Matrix hiddenError = Matrix.MatrixMultiply(weights_ho_t,outputError);

        */
        Matrix outputGradients = Matrix.mapDSigmoid(output);
        outputGradients.multiplySelv(outputError);
        outputGradients.scaleSelv(this.lr);
        Matrix hidden2T = Matrix.transpose(hidden2);
        Matrix weight_h2o_delta = Matrix.MatrixMultiply(outputGradients, hidden2T);
        this.weigts_h2o.addMatrixSelv(weight_h2o_delta);
        /*
        // finder ændring i vægte ----------------------------------
        // finder ændringe i væget i HO og tildeler den
        Matrix gradients = Matrix.mapDSigmoid(output);
        gradients.multiplySelv(outputError);
        gradients.scaleSelv(this.lr);
        Matrix hiddenT = Matrix.transpose(hidden);
        Matrix weight_ho_delta = Matrix.MatrixMultiply(gradients, hiddenT);
        this.weigts_ho.addMatrixSelv(weight_ho_delta);
        */
        Matrix hidden2Gradients = Matrix.mapDSigmoid(hidden2);
        hidden2Gradients.multiplySelv(hidden2Error);
        hidden2Gradients.scaleSelv(this.lr);
        Matrix hidden1T = Matrix.transpose(hidden1);
        Matrix weight_h1h2_delta = Matrix.MatrixMultiply(hidden2Gradients, hidden1T);
        this.weigts_h1h2.addMatrixSelv(weight_h1h2_delta);
        /*
        //finder ændringe i væget i IH og tildeler den
        Matrix hidden_gradient = Matrix.mapDSigmoid(hidden);
        hidden_gradient.multiplySelv(hiddenError);
        hidden_gradient.scaleSelv(this.lr);
        Matrix inputT = Matrix.transpose(input);
        Matrix weight_ih_delta = Matrix.MatrixMultiply(hidden_gradient, inputT);
        this.weigts_ih.addMatrixSelv(weight_ih_delta);
        */
        Matrix hidden1Gradients = Matrix.mapDSigmoid(hidden1);
        hidden1Gradients.multiplySelv(hidden1Error);
        hidden1Gradients.scaleSelv(this.lr);
        Matrix inputT = Matrix.transpose(input);
        Matrix weight_h1i_delta = Matrix.MatrixMultiply(hidden1Gradients, inputT);
        this.weigts_ih1.addMatrixSelv(weight_h1i_delta);
        /*
        //----------------------------------------------------------
        //hent ændig i bias for kodne oven for
        this.bias_o.addMatrixSelv(gradients);
        this.bias_h.addMatrixSelv(hidden_gradient);
         */
        this.bias_o.addMatrixSelv(outputGradients);
        this.bias_h2.addMatrixSelv(hidden2Gradients);
        this.bias_h1.addMatrixSelv(hidden1Gradients);
    }
}
