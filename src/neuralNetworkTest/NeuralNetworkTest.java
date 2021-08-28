package neuralNetworkTest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.*;

import general.Util;

class NeuralNetworkTest extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	
	static NeuralNetworkTest instance;
	static String windowTitle = "Neural Network Test";
	static JMenuBar menuBar = new JMenuBar();
	static JButton resetGenerationButton = new JButton(new AbstractAction("Reset") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			reset();
		}
	});
	static JButton testGenerationButton = new JButton(new AbstractAction("Test") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			testGeneration(neuralNetworkList, true);
		}
	});
	static JButton viewPerformanceButton = new JButton(new AbstractAction("View") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			viewPerformance();
		}
	});
	static JButton newGenerationButton = new JButton(new AbstractAction("New Gen") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			nextGeneration();
		}
	});
	static JButton autoButton = new JButton(new AbstractAction("Auto") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			autotest();
		}
	});
	static JPanel panel = new JPanel(){
		private static final long serialVersionUID = 1L;
		
		@Override
		public void paint(Graphics g){
			draw((Graphics2D)g);
		}
	};
	
	static final int width = 400, height = 400;
	static Color backgroundColor = new Color(200, 200, 200);
	static Color lineColor = new Color(0, 0, 0);
	static Color dotColor = new Color(255, 255, 255);
	
	// Test Parameters //
	static int autotestGenerationGoal = 1000;
	static int autotestAbortThreshold = 100;
	static int displayFPS;
	static int displayStretchX = 1;
	static int stepsToRunFor; 
	static int generationSize;
	static int generationCount;
	static NeuralNetwork[] neuralNetworkList;
	static int best;
	static double bestScore;
	static double meanScore;
	static boolean tested;
	static NeuralNetwork displayedNet = null;
	
	// Test Variables //
	static double score;
	static int x = 0;
	static double y = 0;
	static double f(int x) {
		int[] points = {58, 0, -87, 20, -56, 69, 30};
		return points[x-1];
		
//		return 50*Math.sin(x/100);
//		return x/20 + (x/5)*Math.sin(5000/(500 - x));
	}
	
	private static void autotest() {
		String message = "At generation "+generationCount+". Input target generation.";
		String input = JOptionPane.showInputDialog(message);
		if(input == null) return;
		try {
			autotestGenerationGoal = Integer.parseInt(input);
		} catch(NumberFormatException e) {
			System.out.println("INPUT WAS NOT A NUMBER");
		}
		
		testGeneration(neuralNetworkList, true);
		int failedAdvanceCount = 0;
		while(generationCount < autotestGenerationGoal && failedAdvanceCount < autotestAbortThreshold) {
			boolean advanced = nextGeneration();
			if(!advanced) {
				failedAdvanceCount ++;
				System.out.println("failure to advance "+failedAdvanceCount);
			} else {
				failedAdvanceCount = 0;
			}
		}
		System.out.println("autotest complete");
	}
	
	public static void draw(Graphics2D g){
		Rectangle panelBounds = panel.getBounds();
		g.setColor(backgroundColor);
		g.fillRect(0, 0, panelBounds.width, panelBounds.height);

		// Draw //
		g.translate(10, 150);
		g.setColor(lineColor);
		double prevY, nextY = f(1);
		for(int lineX = 1; lineX < stepsToRunFor; lineX ++) {
			prevY = nextY;
			nextY = f(lineX+1);
			g.drawLine(lineX*displayStretchX, (int)prevY, (lineX+1)*displayStretchX, (int)nextY);
		}
		g.setColor(dotColor);
		Util.fillCircleCentered(x*displayStretchX, y, 3, g);
		System.out.println("x = "+x+", y = "+y);
	}
	
	private static double[] getScoreList(NeuralNetwork[] list) {
		double[] scoreList = new double[list.length];
		for(int i = 0; i < list.length; i ++) {
			scoreList[i] = list[i].score;
		}
		return scoreList;
	}
	
	public static void main(String[] args) {
		instance = new NeuralNetworkTest();
		
		// Parameters //
		displayFPS = 3;
		displayStretchX = 40;
		stepsToRunFor = 7;
		generationSize = 100;
		NeuralNetwork.sensoryNeuronCount = 0;
		NeuralNetwork.conceptNeuronCount = 2;
		NeuralNetwork.memoryNeuronCount = 2;
		NeuralNetwork.motorNeuronCount = 1;
		autotestAbortThreshold = Integer.MAX_VALUE;
		
		reset();
	}
	
	private static NeuralNetwork[] newGeneration() {
		if(!tested) {
			JOptionPane.showMessageDialog(null, "This generation has not been tested yet", "error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		tested = false;
		generationCount ++;
		
		// Create a new generation //
		double totalScore = M.sum(getScoreList(neuralNetworkList));
		
		NeuralNetwork[] newNetList = new NeuralNetwork[generationSize];
		for(int i = 0; i < newNetList.length; i ++) {
			// Choose a net to clone based on scores //
			double roll = M.rand(totalScore);
			int selection = -1;
			do {
				selection ++;
				roll -= neuralNetworkList[selection].score;
			} while(roll > 0);
			
			// Clone the chosen net //
			newNetList[i] = new NeuralNetwork(neuralNetworkList[selection]);
			newNetList[i].name = "Gen "+generationCount+" Net "+i;
		}
		return newNetList;
	}
	
	private static boolean nextGeneration() {
		// Save the old generation's scores //
		double oldMeanScore = meanScore;
		
		// Create and test a new generation //
		NeuralNetwork[] newGeneration = newGeneration();
		if(newGeneration == null) return false;
		testGeneration(newGeneration, false);
		
		// Get best of both generations //
		LinkedList<NeuralNetwork> netList = new LinkedList<NeuralNetwork>();
		for(NeuralNetwork net : neuralNetworkList) {
			netList.add(net);
		}
		for(NeuralNetwork net : newGeneration) {
			netList.add(net);
		}
		for(int i = 0; i < newGeneration.length; i ++) {
			NeuralNetwork bestNet = null;
			double bestScore = 0.0;
			for(NeuralNetwork net : netList) {
				if(net.score >= bestScore) {
					bestNet = net;
					bestScore = net.score;
				}
			}
			newGeneration[i] = bestNet;
			netList.remove(bestNet);
		}
		
		// Recalculate best/mean scores and rename new generation //
		double[] scoreList = getScoreList(newGeneration);
		bestScore = 0.0;
		for(int i = 0; i < newGeneration.length; i ++) {
			if(newGeneration[i].score > bestScore) {
				best = i;
				bestScore = newGeneration[i].score;
			}
			
			// Rename //
			newGeneration[i].name = "Gen "+generationCount+" Net "+i;
		}
		meanScore = M.mean(scoreList);
		neuralNetworkList = newGeneration;
		tested = true;
		System.out.println("advanced to generation "+generationCount);
		return (meanScore > oldMeanScore);
	}
	
	private static void reset() {
		neuralNetworkList = new NeuralNetwork[generationSize];
		tested = false;
		generationCount = 0;
		for(int i = 0; i < generationSize; i ++) {
			neuralNetworkList[i] = new NeuralNetwork("Gen 0 Net "+i);
		}
	}
	
	private static void setupTest(NeuralNetwork netToTest) {
		displayedNet = netToTest;
		displayedNet.wipeMemory();
		score = 0;
		x = 0;
		y = 0;
	}
	
	private static void step() {
		if(displayedNet != null) {
//			Object[] sensoryInputs = {Double.valueOf(y - f(x))};
			displayedNet.step(null);
			x ++;
			y = displayedNet.output;

			double scoreThisStep = 1.0 - Math.abs(M.realToInterval( (y - f(x)) ));
//			double scoreThisStep = 1.0 - Math.abs(M.realToInterval( (y - f(x))/100 ));
			score += scoreThisStep;
		}
	}
	
	private static void testGeneration(NeuralNetwork[] generationToTest, boolean log) {
		best = 0;
		bestScore = 0.0;
		if(log) System.out.print("testing");
		for(int i = 0; i < generationToTest.length; i ++) {
			if(log) System.out.print(".");
			
			// Perform a full test for this net //
			setupTest(generationToTest[i]);
			for(int step = 0; step < stepsToRunFor; step ++) {
				step();
			}
			
			// Record the score //
			generationToTest[i].score = score;
			if(score > bestScore) {
				best = i;
				bestScore = score;
			}
		}
		tested = true;
		meanScore = M.mean(getScoreList(neuralNetworkList));
		if(log) {
			System.out.println("done");
			System.out.println("mean score = "+meanScore);
			System.out.println("best score = "+bestScore+" by "+generationToTest[best]);
		}
	}
	
	private static void viewPerformance() {
		NeuralNetwork selected = (NeuralNetwork)JOptionPane.showInputDialog(
				null, null, "Select a neural net", JOptionPane.PLAIN_MESSAGE, null, neuralNetworkList, neuralNetworkList[0]);
		if(selected != null) {
			viewPerformanceButton.setEnabled(false);
			setupTest(selected);
			new Thread(instance).start();
		}
	}
	
	NeuralNetworkTest() {
		setResizable(true);
		setSize(width, height);
		setTitle(windowTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		add(menuBar, BorderLayout.NORTH);
		menuBar.add(resetGenerationButton);
		menuBar.add(testGenerationButton);
		menuBar.add(viewPerformanceButton);
		menuBar.add(newGenerationButton);
		menuBar.add(autoButton);
		setVisible(true);
		createBufferStrategy(2);
	}
	
	public void run() {
		System.out.println("viewing "+displayedNet);
		for(int i = 0; i < stepsToRunFor; i ++) {
			step();
			repaint();
			try{
				Thread.sleep(1000/displayFPS);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		System.out.println("score = "+score);
		viewPerformanceButton.setEnabled(true);
	}
}