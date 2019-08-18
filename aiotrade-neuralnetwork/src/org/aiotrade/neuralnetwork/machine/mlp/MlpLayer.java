/*
 * Copyright (c) 2006-2007, AIOTrade Computing Co. and Contributors
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *    
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *    
 *  o Neither the name of AIOTrade Computing Co. nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.aiotrade.neuralnetwork.machine.mlp;

import org.aiotrade.neuralnetwork.core.model.Layer;
import org.aiotrade.neuralnetwork.core.model.Neuron;
import org.aiotrade.neuralnetwork.machine.mlp.neuron.PerceptronNeuron;
import java.util.List;

/**
 * 
 * @author Caoyuan Deng
 */
public abstract class MlpLayer extends Layer {
    
    public MlpLayer(Layer nextLayer, int inputDimension, int nNeurons, String neuronClassName) {
        this(nextLayer, inputDimension, nNeurons, neuronClassName, true);
    }
    
    public MlpLayer(Layer nextLayer, int inputDimension, int nNeurons, String neuronClassName, boolean hidden) {
        super(nextLayer, inputDimension);
        
        try {
            for (int i = 0; i < nNeurons; i++) {
                PerceptronNeuron neuron = (PerceptronNeuron)Class.forName(neuronClassName).newInstance();
                neuron.init(inputDimension, hidden);
                addNeuron(neuron);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void backPropagateFromNextLayerOrExpectedOutput() {
        computeNeuronsDelta();
        computeNeuronsGradientAndSumIt();
    }
    
    /** 
     * For hidden layer @see MlpHiddenLayer#computeNeuronsDelta()
     * For output layer @see MlpOutputLayer#computeNeuronsDelta()
     */
    protected abstract void computeNeuronsDelta();
    
    protected void computeNeuronsGradientAndSumIt() {
        List<Neuron> neurons = getNeurons();
        for (int i = 0, n = getNNeurons(); i < n; i++) {
            PerceptronNeuron neuron = (PerceptronNeuron)neurons.get(i);
            
            neuron.getLearner().computeGradientAndSumIt();
        }
    }
    
    public void adapt(double learningRate, double momentumRate) {
        List<Neuron> neurons = getNeurons();
        for (int i = 0, n = neurons.size(); i < n; i++) {
            PerceptronNeuron neuron = (PerceptronNeuron)neurons.get(i);
            
            neuron.adapt(learningRate, momentumRate);
        }
    }
    
    public MlpLayer getNextLayer() {
        return (MlpLayer)super.getNextLayer();
    }
    
}