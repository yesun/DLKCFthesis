package graph;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import model.RoadModel;

import speedFunction.SpeedFunction;
import trueSolution.TrueSolution;

import filters.Estimation;

@SuppressWarnings("serial")
public class FilterChoice extends JPanel implements ActionListener, MouseListener{

	JList<String> currentFilters = new JList<String>();
	DefaultListModel<String> currentList = new DefaultListModel<String>();
	
	String evolutionModel;
	String observationModel;
	String nameFilter;
	String nameSpeedFunction;
	Boolean isAdditive;
	Boolean isAdaptive;
	String nameTrue;
	
	static String filterDefault = "EnKF";
	static String evolutionDefault = "S";
	static String observationDefault = "speed";
	static String speedDefault = "SmuldersGen";
	static Boolean additiveDefault = true;
	static Boolean adaptiveDefault = false;
	static String nameTrueDefault = "Smulders";
	
	int cells;
	double rhoLeft; 
	double rhoRight;
	double dt;
	double dx;
	TrueSolution trueSolution;
	
	LinkedList<Estimation> listEstimations = new LinkedList<Estimation>();
	
	public FilterChoice(double _rhoLeft, double _rhoRight, double _dt, double _dx, int _cells) {
		
		BoxLayout layout = new BoxLayout(this,BoxLayout.Y_AXIS);
		setLayout(layout);
		
		cells = _cells;
		dt = _dt;
		dx = _dx;
		rhoLeft = _rhoLeft;
		rhoRight = _rhoRight;
		
		JPanel choicePanel = new JPanel();
		
		JPanel subChoicePanel = new JPanel();
		BoxLayout layout2 = new BoxLayout(subChoicePanel,BoxLayout.Y_AXIS);
		subChoicePanel.setLayout(layout2);
		
		subChoicePanel.add(makeTruePanel());
		
		subChoicePanel.add(makeFilterPanel());
		subChoicePanel.add(makeNoisePanel());
		choicePanel.add(subChoicePanel);
		
		choicePanel.add(makeModelPanel());
		
		JPanel subChoicePanel2 = new JPanel();
		BoxLayout layout3 = new BoxLayout(subChoicePanel2,BoxLayout.Y_AXIS);
		subChoicePanel2.setLayout(layout3);
		subChoicePanel2.add(makeSpeedPanel());
		subChoicePanel2.add(makeAdaptivePanel());
		choicePanel.add(subChoicePanel2);
		
 		JPanel buttonPanel = new JPanel();
		JButton addFilter = new JButton("Add filter");
		addFilter.setActionCommand("addFilter");
		addFilter.addActionListener(this);		
 		buttonPanel.add(addFilter);
 		
 		JButton clearThis = new JButton("Clear this filter");
 		clearThis.setActionCommand("clearThis");
 		clearThis.addActionListener(this);		
 		buttonPanel.add(clearThis);
 		
 		JButton clearList = new JButton("Clear all filters");
 		clearList.setActionCommand("clearList");
 		clearList.addActionListener(this);		
 		buttonPanel.add(clearList);
 	

 		add(choicePanel);
 		add(buttonPanel);
 		
 		JPanel somePanel = new JPanel();
 		somePanel.add(new JLabel("Current Filters : "));
 		add(somePanel);
 		
 		currentFilters.addMouseListener(this);
 		currentFilters.setBackground(Color.white); 	
 		add(currentFilters);
 		addFilter();		
	}
	
	public void actionPerformed(ActionEvent event) {
		String nameAction = event.getActionCommand();
		if (nameAction.equals("setTrue")) setTrue();
		else if (nameAction.equals("addFilter")) addFilter();
		else if (nameAction.equals("clearThis")) removeFilter();
		else if (nameAction.equals("clearList")) {
			listEstimations.clear();
			currentList.clear();
			currentFilters.setModel(currentList);
		}
		else if (nameAction.equals("add")) {
			isAdditive = true;
		}
		else if (nameAction.equals("nonadd")) {
			isAdditive = false;
		}
		else if (nameAction.equals("apt")) {
			isAdaptive = true;
		}
		else if (nameAction.equals("nonapt")) {
			isAdaptive = false;
		}
		else if (nameAction.startsWith("m1")) {
			evolutionModel = nameAction.substring(2);
		}
		else if (nameAction.startsWith("m2")) {
			observationModel = nameAction.substring(2);
		}
		else if (nameAction.startsWith("s ")) {
			nameSpeedFunction = nameAction.substring(2);
		}
		else if (nameAction.startsWith("f ")) {
			nameFilter = nameAction.substring(2);
		}
		else if (nameAction.startsWith("t ")) {
			nameTrue = nameAction.substring(2);
		}
	}
	
	private void setTrue() {
		listEstimations.clear();
		currentList.clear();
		currentFilters.setModel(currentList);
		trueSolution = TrueSolution.createTrueSolution(nameTrue+"True", rhoLeft, rhoRight, dt, dx, cells);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel makeTruePanel() {
		JPanel truePanel = new JPanel(new GridLayout(0,1));
		ButtonGroup trueGroup = new ButtonGroup();
		
		File file = new File(ControlPanel.absolutePath+"trueSolution");
		
		File[] listFile = file.listFiles();
		truePanel.add(new JLabel("True speed function:"));
		try {
			Class clMaster = Class.forName("trueSolution.TrueSolution");
			for (int i=0; i<listFile.length; i++ ) {
				
				String name = listFile[i].getName().split("\\.")[0];
				String fullName = "trueSolution."+name;
				
				Class cl = Class.forName(fullName);
				int mod = cl.getModifiers();
				if ((!Modifier.isAbstract(mod))&&(clMaster.isAssignableFrom(cl))) {
					JRadioButton trueButton;
					name = name.substring(0, name.length()-4);
					if (name.equals(nameTrueDefault)) {
						nameTrue = nameTrueDefault;
						trueButton   = new JRadioButton(name  , true);
					}
					else trueButton   = new JRadioButton(name  , false);
					trueButton.addActionListener(this);
					trueButton.setActionCommand("t "+name);
					trueGroup.add(trueButton);
					truePanel.add(trueButton);
				}
			}
 		}
 		catch(Throwable t) {System.out.println(t);}
		JButton setTrue = new JButton("Set true solution");
		setTrue.addActionListener(this);
		setTrue.setActionCommand("setTrue");
		truePanel.add(setTrue);
		trueSolution = TrueSolution.createTrueSolution(nameTrue+"True", rhoLeft, rhoRight, dt, dx, cells);
 		return truePanel;
	}

	public void addFilter() {
		String noise;
		if (isAdditive) noise = "Additive noise";
		else noise = "Non additive noise";
		String adaptive;
		if (isAdaptive) adaptive = "Adaptive SF";
		else adaptive = "Non adaptive SF";
		String nameModel = evolutionModel+"_"+observationModel;
		String type = "Filter "+Integer.toString(currentList.size())+" = "+nameFilter+" / "+nameModel+" / "
			+nameSpeedFunction+" / "+noise+" / "+adaptive;
		currentList.addElement(type);
		currentFilters.setModel(currentList);
		listEstimations.add(
				new Estimation(RoadModel.createRoadModel(trueSolution,evolutionModel,observationModel,nameSpeedFunction,isAdaptive),
				nameFilter,!isAdditive));
	}
	
	public void removeFilter() {
		int index = currentFilters.getSelectedIndex();
		if (index>=0){
			listEstimations.remove(index);
			currentList.remove(index);
			for (int i = 0; i<currentList.size(); i++) {
				String s = currentList.get(i);
				String sNew = s.substring(0, 7)+Integer.toString(i)+s.substring(8);
				currentList.set(i, sNew);
			}
			currentFilters.setModel(currentList);
		}
	}
	
	public void newBoundaries(double _rhoLeft, double _rhoRight) {
		rhoLeft = _rhoLeft;
		rhoRight = _rhoRight;
		for (Estimation estim : listEstimations) {
			estim.newBoundaries(rhoLeft, rhoRight);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JPanel makeFilterPanel() {
		JPanel filterPanel = new JPanel(new GridLayout(0,1));
		ButtonGroup filterGroup = new ButtonGroup();
		File file = new File(ControlPanel.absolutePath+"filters");
		File[] listFile = file.listFiles();
 		filterPanel.add(new JLabel("Filter:"));
		try {
			for (int i=0; i<listFile.length; i++ ) {
				String name = listFile[i].getName().split("\\.")[0];
				String fullName = "filters."+name;
				Class clMaster = Class.forName("filters.Filter");
				Class cl = Class.forName(fullName);
				int mod = cl.getModifiers();
				if ((!Modifier.isAbstract(mod))&&(clMaster.isAssignableFrom(cl))) {
					JRadioButton filterButton;
					if (name.equals(filterDefault)) {
						filterButton   = new JRadioButton(name  , true);
						nameFilter = name;
					}
					else filterButton   = new JRadioButton(name  , false);
					filterButton.addActionListener(this);
					filterButton.setActionCommand("f "+name);
					filterGroup.add(filterButton);
					filterPanel.add(filterButton);
				}
			}
 		}
        catch(Throwable t) {System.out.println(t);}
 		return filterPanel;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel makeModelPanel() {
		JPanel modelPanel = new JPanel(new GridLayout(0,1));

		HashSet<String> evolutionList = new HashSet<String>();

		File file = new File(ControlPanel.absolutePath+"model");
		File[] listFile = file.listFiles();
 		try {
			Class clMaster = Class.forName("model.RoadModel");
			for (int i=0; i<listFile.length; i++ ) {
				
				String name = listFile[i].getName().split("\\.")[0];
				String fullName = "model."+name;
				
				Class cl = Class.forName(fullName);
				int mod = cl.getModifiers();
				if ((!Modifier.isAbstract(mod))&&(clMaster.isAssignableFrom(cl))) {
					evolutionList.add(name);
				}
			}
			
			modelPanel.add(new JLabel("Evolution:"));
			ButtonGroup evolutionGroup = new ButtonGroup();
			String[] evList = new String[evolutionList.size()];
			evolutionList.toArray(evList);
			Arrays.sort(evList);
			for (String s : evList) {
				JRadioButton evolutionButton;
				if (s.equals(evolutionDefault)) {
					evolutionModel = s;
					evolutionButton =  new JRadioButton(s  , true);
				}
				else evolutionButton =  new JRadioButton(s  , false);
				evolutionButton.addActionListener(this);
				evolutionButton.setActionCommand("m1"+s);
				evolutionGroup.add(evolutionButton);
				modelPanel.add(evolutionButton);
			}
			
			modelPanel.add(new JLabel("Observation:"));
			ButtonGroup observationGroup = new ButtonGroup();
			String[] obList = {"density", "speed" ,"density & speed"};
			Arrays.sort(obList);
			for (String s : obList) {
				JRadioButton observationButton;
				if (s.equals(observationDefault)) {
					observationModel = s;
					observationButton =  new JRadioButton(s  , true);
				}
				else observationButton =  new JRadioButton(s  , false);
				observationButton.addActionListener(this);
				observationButton.setActionCommand("m2"+s);
				observationGroup.add(observationButton);
				modelPanel.add(observationButton);
			}
			
 		}
 		catch(Throwable t) {System.out.println(t);}
 		return modelPanel;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private JPanel makeSpeedPanel() {
		JPanel mainSpeedPanel = new JPanel();
		JPanel speedPanel = new JPanel(new GridLayout(0,1));
		JPanel speedParamPanel = new JPanel(new GridLayout(0,1));
		ButtonGroup speedGroup = new ButtonGroup();
		speedPanel.add(new JLabel("Filter speed function:"));
		File file = new File(ControlPanel.absolutePath+"speedFunction");
		File[] listFile = file.listFiles();
 		try {
			for (int i=0; i<listFile.length; i++ ) {
				
				final String name = listFile[i].getName().split("\\.")[0];
				String fullName = "speedFunction."+name;
				
				Class cl = Class.forName(fullName);
				int mod = cl.getModifiers();
				if (!Modifier.isAbstract(mod)) {
					JRadioButton speedButton;
					if (name.equals(speedDefault)) {
						speedButton  = new JRadioButton(name  , true);
						nameSpeedFunction = name;
					}
					else speedButton   = new JRadioButton(name  , false);
					speedButton.addActionListener(this);
					speedButton.setActionCommand("s "+name);
					
					JButton setButton = new JButton("Set");
					setButton.setActionCommand(name);
					setButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent actionEvent) {
							SpeedFunction sf = SpeedFunction.createSpeedFunction(actionEvent.getActionCommand());
							new SFparamChoice(sf);
						}
					});
					speedParamPanel.add(setButton);

					speedGroup.add(speedButton);
					speedPanel.add(speedButton);
				}
			}
 		}
 		catch(Throwable t) {System.out.println(t);}
 		mainSpeedPanel.add(speedPanel);
 		mainSpeedPanel.add(speedParamPanel);
 		return mainSpeedPanel;
	}
	
	private JPanel makeNoisePanel() {
		JPanel noisePanel = new JPanel(new GridLayout(0,1));
		ButtonGroup noiseGroup = new ButtonGroup();
		noisePanel.add(new JLabel("Type of noise:"));
		isAdditive = additiveDefault;
		JRadioButton noiseButton   = new JRadioButton("Additive noise"  , additiveDefault);
		noiseButton.addActionListener(this);
		noiseButton.setActionCommand("add");
		noiseGroup.add(noiseButton);
		noisePanel.add(noiseButton);
		
		noiseButton   = new JRadioButton("Non additive noise"  , !additiveDefault);
		noiseButton.addActionListener(this);
		noiseButton.setActionCommand("nonadd");
		noiseGroup.add(noiseButton);
		noisePanel.add(noiseButton);
		return noisePanel;
		
	}

	private JPanel makeAdaptivePanel() {
		JPanel adaptivePanel = new JPanel(new GridLayout(0,1));
		ButtonGroup adaptiveGroup = new ButtonGroup();

		isAdaptive = adaptiveDefault;
		JRadioButton adaptiveButton   = new JRadioButton("Adaptive SF"  , adaptiveDefault);
		adaptiveButton.addActionListener(this);
		adaptiveButton.setActionCommand("apt");
		adaptiveGroup.add(adaptiveButton);
		adaptivePanel.add(adaptiveButton);
		
		adaptiveButton   = new JRadioButton("Non adaptive SF"  , !adaptiveDefault);
		adaptiveButton.addActionListener(this);
		adaptiveButton.setActionCommand("nonapt");
		adaptiveGroup.add(adaptiveButton);
		adaptivePanel.add(adaptiveButton);
		return adaptivePanel;
		
	}
	
	
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) removeFilter();
	}

	
	public void mouseEntered(MouseEvent arg0) {
	}


	public void mouseExited(MouseEvent arg0) {
	}

	
	public void mousePressed(MouseEvent arg0) {
	}


	public void mouseReleased(MouseEvent arg0) {
	}
	
	

	
	
}
