package neuralNetworkTest;

class M extends maths.M {
	
	static String abbreviate(int i) {
		String sign = (i < 0) ? "-" : "";
		String suffix = "";
		i = Math.abs(i);
		if(1000 < i && i < 1000000) {
			i = i/1000;
			suffix = "k";
		} else if(1000000 < i && i < 1000000000) {
			i = i/1000000;
			suffix = "M";
		}
		return sign+i+suffix;
	}
	
	static double[][] cloneMatrix(double[][] matrix){
		if(matrix.length == 0) {
			return new double[0][0];
		} else {
			double[][] clone = new double[matrix.length][matrix[0].length];
			for(int row = 0; row < matrix.length; row ++) {
				for(int col = 0; col < matrix[row].length; col ++) {
					clone[row][col] = matrix[row][col];
				}
			}
			return clone;
		}
	}
	
	static double[] cloneVector(double[] vector){
		double[] clone = new double[vector.length];
		for(int i = 0; i < vector.length; i ++) {
			clone[i] = vector[i];
		}
		return clone;
	}
	
	static double normalise(double value) {
		if(value < 0) {
			return 0.0;
		} else if(value > 1.0){
			return 1.0;
		} else {
			return value;
		}
	}

	static double[][] mergeMatrices(double[][] matrix1, double[][] matrix2){
		if(matrix1.length == 0) {
			return new double[0][0];
		} else {
			double[][] clone = new double[matrix1.length][matrix1[0].length];
			for(int row = 0; row < matrix1.length; row ++) {
				for(int col = 0; col < matrix1[row].length; col ++) {
					clone[row][col] = (M.roll(0.5)) ? matrix1[row][col] : matrix2[row][col];
				}
			}
			return clone;
		}
	}
	static double[] mergeVectors(double[] vector1, double[] vector2){
		double[] clone = new double[vector1.length];
		for(int i = 0; i < vector1.length; i ++) {
			clone[i] = (M.roll(0.5)) ? vector1[i] : vector2[i];
		}
		return clone;
	}
	
	static float mutateFloat(float value){
		float dice = M.randf(1.0f);
		dice = dice*dice*dice;
		float mutatedValue = M.roll(0.5) ? value + dice*(1.0f - value) : value - dice*value;
		if(mutatedValue <= 0 || mutatedValue >= 1){
			return value;
		} else {
			return mutatedValue;
		}
	}
	
	/**
	 * Writes the entries of overwriter onto overwritee. Overwritee is assumed to be no smaller than overwriter.
	 * @param overwritee
	 * @param overwriter
	 */
	static double[][] overwriteMatrix(double[][] overwritee, double[][] overwriter) {
		for(int row = 0; row < overwriter.length; row ++) {
			for(int col = 0; col < overwriter[row].length; col ++) {
				overwritee[row][col] = overwriter[row][col];
			}
		}
		return overwritee;
	}
	
	/**
	 * Writes the entries of overwriter onto overwritee. Overwritee is assumed to be no smaller than overwriter.
	 * @param overwritee
	 * @param overwriter
	 */
	static double[] overwriteVector(double[] overwritee, double[] overwriter) {
		for(int i = 0; i < overwriter.length; i ++) {
			overwritee[i] = overwriter[i];
		}
		return overwritee;
	}
	
	/**
	 * Scales a real number x to be in the open interval (-1, 1).
	 * @param x
	 * @return
	 */
	static double realToInterval(double x) {
		return 2.0*Math.atan(x)/Math.PI;
	}
	
	static double realToIntervalInverse(double x) {
		return Math.tan(Math.PI*x/2.0);
	}
	
	/**
	 * Scales a real number x to be in the open interval (0, 1).
	 * @param x
	 * @return
	 */
	static double realToPositiveInterval(double x) {
		return 0.5 + Math.atan(x)/Math.PI;
	}
	
	static double realToPositiveIntervalInverse(double x) {
		return Math.tan((x - 0.5)*Math.PI);
	}
	
	static void setRandomEntries(double[][] matrix, double min, double max) {
		for(int row = 0; row < matrix.length; row ++) {
			for(int col = 0; col < matrix[row].length; col ++) {
				matrix[row][col] = rand(min, max);
			}
		}
	}
	
	static void setRandomEntries(double[] vector, double min, double max) {
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = rand(min, max);
		}
	}
	
	static double[][] shrinkMatrixCols(double[][] matrix, int columnToRemove) {
		if(matrix.length == 0) {
			return new double[0][0];
		} else {
			double[][] clone = new double[matrix.length][matrix[0].length - 1];
			for(int row = 0; row < matrix.length; row ++) {
				for(int col = 0; col < columnToRemove; col ++) {
					clone[row][col] = matrix[row][col];
				}
				for(int col = columnToRemove + 1; col < matrix[row].length; col ++) {
					clone[row][col - 1] = matrix[row][col];
				}
			}
			return clone;
		}
	}
	
	static double[][] shrinkMatrixRows(double[][] matrix, int rowToRemove) {
		if(matrix.length <= 1) {
			return new double[0][0];
		} else {
			double[][] clone = new double[matrix.length - 1][matrix[0].length];
			for(int row = 0; row < rowToRemove; row ++) {
				for(int col = 0; col < matrix[row].length; col ++) {
					clone[row][col] = matrix[row][col];
				}
			}
			for(int row = rowToRemove + 1; row < matrix.length; row ++) {
				for(int col = 0; col < matrix[row].length; col ++) {
					clone[row - 1][col] = matrix[row][col];
				}
			}
			return clone;
		}
	}
	
	static double[] shrinkVector(double[] vector, int coordinateToRemove) {
		double[] clone = new double[vector.length - 1];
		for(int i = 0; i < coordinateToRemove; i ++) {
			clone[i] = vector[i];
		}
		for(int i = coordinateToRemove + 1; i < vector.length; i ++) {
			clone[i - 1] = vector[i];
		}
		return clone;
	}
}