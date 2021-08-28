package neuralNetworkTest;

class NeuralNetwork {
	static int sensoryNeuronCount, memoryNeuronCount, conceptNeuronCount, motorNeuronCount;
	static double mutationProbability = 0.05;
	
	// Metadata //
	String name = "";
	double score = Double.NaN;
	
	// Physical //
	double output;
	
	// Neurons //
	double[] sensoryNeurons;
	double[] memoryNeurons;
	double[] conceptNeurons;
	double[] motorNeurons;
	
	// Connection layer 1 //
	double[][] sensoryConceptConnections;
	double[][] memoryConceptConnections;
	double[] conceptBias;
	
	// Connection layer 2 //
	double[][] conceptMotorConnections;
	double[] motorBias;
	double[][] conceptMemoryConnections;
	double[] memoryBias;
	
	protected static double mutateDoubleSigned(double value) {
		double perturbation = M.rand(1.0);
		perturbation = perturbation*perturbation*perturbation*perturbation;
		boolean increase = M.roll(0.5);
		if(increase) {
			value = value + (1.0 - value)*perturbation;
		} else {
			value = value + (-1.0 - value)*perturbation;
		}
		return value;
		////////////////////////////////////////////////////////////////
//		return M.rand(1)*M.rand(1)*M.rand(1) - M.rand(1)*M.rand(1)*M.rand(1); // This seems to struggle with fine adjustments, as one would expect.
	}
	
	protected static double mutateDoubleSigned(double value, double probability) {
		return M.roll(probability) ? mutateDoubleSigned(value) : value;
	}
	
	protected static void mutateMatrix(double[][] matrix, double probability) {
		for(int i = 0; i < matrix.length; i ++) {
			for(int j = 0; j < matrix[i].length; j ++) {
				matrix[i][j] = mutateDoubleSigned(matrix[i][j], probability);
			}
		}
	}
	
	protected static void mutateVector(double[] vector, double probability) {
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = mutateDoubleSigned(vector[i], probability);
		}
	}
	
	NeuralNetwork(String name) {
		this.name = name;
		setupNeuralNetwork();
		initialiseNeuralNetworkRandomly();
	}
	
	NeuralNetwork(NeuralNetwork parent) {
		this.name = parent.name+" clone";
		setupNeuralNetwork();
		initialiseNeuralNetworkCloningParent(parent);
		mutate();
	}
	
	private void initialiseNeuralNetworkCloningParent(NeuralNetwork parent) {
		sensoryConceptConnections = M.cloneMatrix(parent.sensoryConceptConnections);
		memoryConceptConnections = M.cloneMatrix(parent.memoryConceptConnections);
		conceptBias = M.cloneVector(parent.conceptBias);
		conceptMotorConnections = M.cloneMatrix(parent.conceptMotorConnections);
		motorBias = M.cloneVector(parent.motorBias);
		conceptMemoryConnections = M.cloneMatrix(parent.conceptMemoryConnections);
		memoryBias = M.cloneVector(parent.memoryBias);
	}
	
	private void initialiseNeuralNetworkRandomly() {
		double min = -1, max = 1;
		M.setRandomEntries(sensoryConceptConnections, min, max);
		M.setRandomEntries(memoryConceptConnections, min, max);
		M.setRandomEntries(conceptBias, min, max);
		M.setRandomEntries(conceptMotorConnections, min, max);
		M.setRandomEntries(motorBias, min, max);
		M.setRandomEntries(conceptMemoryConnections, min, max);
		M.setRandomEntries(memoryBias, min, max);
	}
	
	private void mutate() {
		mutateMatrix(sensoryConceptConnections, mutationProbability);
		mutateMatrix(memoryConceptConnections, mutationProbability);
		mutateVector(conceptBias, mutationProbability);
		mutateMatrix(conceptMotorConnections, mutationProbability);
		mutateVector(motorBias, mutationProbability);
		mutateMatrix(conceptMemoryConnections, mutationProbability);
		mutateVector(memoryBias, mutationProbability);
	}
	
	private void setupNeuralNetwork() {
		// Neurons //
		sensoryNeurons = new double[sensoryNeuronCount];
		memoryNeurons = new double[memoryNeuronCount];
		conceptNeurons = new double[conceptNeuronCount];
		motorNeurons = new double[motorNeuronCount];
		
		// Connection layer 1 //
		sensoryConceptConnections = new double[conceptNeurons.length][sensoryNeurons.length];
		memoryConceptConnections = new double[conceptNeurons.length][memoryNeurons.length];
		conceptBias = new double[conceptNeurons.length];
		
		// Connection layer 2 //
		conceptMotorConnections = new double[motorNeurons.length][conceptNeurons.length];
		motorBias = new double[motorNeurons.length];
		conceptMemoryConnections = new double[memoryNeurons.length][conceptNeurons.length];
		memoryBias = new double[memoryNeurons.length];
	}
	
	public void step(Object[] sensoryInputs) {
		// Parse sensory inputs //
//		double dy = (Double)sensoryInputs[0];
		
		// Set sensory neurons //
//		sensoryNeurons[0] = M.realToPositiveInterval(dy);
		
		// Evaluate connections to concept neurons //
		for(int i = 0; i < conceptNeurons.length; i ++) {
			// Set neuron to the bias value. //
			conceptNeurons[i] = conceptBias[i];
			
			// Add the sensory connections. //
			for(int j = 0; j < sensoryNeurons.length; j ++) {
				conceptNeurons[i] += sensoryConceptConnections[i][j]*sensoryNeurons[j];
			}
			
			// Add the memory connections. //
			for(int j = 0; j < memoryNeurons.length; j ++) {
				conceptNeurons[i] += memoryConceptConnections[i][j]*memoryNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			M.normalise(conceptNeurons[i]);
//			conceptNeurons[i] /= 1 + sensoryNeurons.length + memoryNeurons.length;
		}
		
		// Evaluate connections to motor neurons //
		for(int i = 0; i < motorNeurons.length; i ++) {
			// Set neuron to the bias value. //
			motorNeurons[i] = motorBias[i];
			
			// Add the concept connections. //
			for(int j = 0; j < conceptNeurons.length; j ++) {
				motorNeurons[i] += conceptMotorConnections[i][j]*conceptNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			motorNeurons[i] = M.normalise(motorNeurons[i]);
//			motorNeurons[i] /= 1 + conceptNeurons.length;
		}
		
		// Evaluate connections to memory neurons //
		for(int i = 0; i < memoryNeurons.length; i ++) {
			// Set neuron to the bias value. //
			memoryNeurons[i] = memoryBias[i];
			
			// Add the concept connections. //
			for(int j = 0; j < conceptNeurons.length; j ++) {
				memoryNeurons[i] += conceptMemoryConnections[i][j]*conceptNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			memoryNeurons[i] = M.normalise(memoryNeurons[i]);
//			memoryNeurons[i] /= 1 + conceptNeurons.length;
		}
		
		// Perform motor outputs //
		this.output = M.realToPositiveIntervalInverse(motorNeurons[0]);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void wipeMemory() {
		for(int i = 0; i < memoryNeurons.length; i ++) {
			memoryNeurons[i] = 0.0;
		}
	}
}