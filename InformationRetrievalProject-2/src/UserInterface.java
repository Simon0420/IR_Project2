import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class UserInterface extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JLabel labelQuery = new JLabel("Query: ");
	private JTextField textQuery = new JTextField(40);
	private JButton search = new JButton("Search");
	private JButton delete = new JButton("Delete");
	
	private JLabel labelConfigPreprocessing = new JLabel("Pre-processing: ");
	private JLabel labelRankingFunctions = new JLabel("Ranking-f(x)s: ");
	
	public UserInterface() {
		super("Information Retrieval System");
		JPanel pane = new JPanel();	
		getContentPane().setLayout(
			    new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
			);
		
		//QUERY PANEL
		JPanel queryPanel = new JPanel(new GridBagLayout());
		GridBagConstraints queryConstraints = new GridBagConstraints();
		queryConstraints.anchor = GridBagConstraints.WEST;
		queryConstraints.insets = new Insets(5, 5, 5, 5);
		queryConstraints.gridx = 0;
		queryConstraints.gridy = 0;
		queryPanel.add(labelQuery, queryConstraints);
		queryConstraints.gridx = 1;
		queryConstraints.gridy = 0;
		queryConstraints.gridwidth = 2;
		queryPanel.add(textQuery, queryConstraints);
		queryConstraints.gridx = 1;
		queryConstraints.gridy = 1;
		queryConstraints.gridwidth = 1;
		queryPanel.add(search, queryConstraints);		
		queryConstraints.gridx = 2;
		queryConstraints.gridy = 1;
		queryPanel.add(delete, queryConstraints);
		queryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Query Panel"));
		getContentPane().add(queryPanel);
		
		//CONFIG PANEL
		JPanel configPanel = new JPanel(new GridBagLayout());
		GridBagConstraints configConstraints = new GridBagConstraints();
		configConstraints.insets = new Insets(5, 5, 5, 5);
		
	    JRadioButton nlpStemmer = new JRadioButton("CoreNLP Stemmer");
	    nlpStemmer.setActionCommand("nlpStemmer");
	    nlpStemmer.setSelected(true);
	    JRadioButton nlpLemmatizer = new JRadioButton("CoreNLP Lemmatizer");
	    nlpLemmatizer.setActionCommand("nlpLemmatizer");
	    JRadioButton group11Stemmer = new JRadioButton("Group#11 Stemmer");
	    group11Stemmer.setActionCommand("group11Stemmer");
	    //Group the radio buttons.
	    ButtonGroup ppGroup = new ButtonGroup();
	    ppGroup.add(nlpStemmer);
	    ppGroup.add(nlpLemmatizer);
	    ppGroup.add(group11Stemmer);	
	    
	    JRadioButton bim = new JRadioButton("BIM");
	    bim.setActionCommand("bim");
	    bim.setSelected(true);
	    JRadioButton twoP = new JRadioButton("2-P");
	    twoP.setActionCommand("twoP");
	    JRadioButton bm11 = new JRadioButton("BM11");
	    bm11.setActionCommand("bm11");
	    JRadioButton bm25 = new JRadioButton("BM25");
	    bm25.setActionCommand("bm25");
	    //Group the radio buttons.
	    ButtonGroup rankGroup = new ButtonGroup();
	    rankGroup.add(bim);
	    rankGroup.add(twoP);
	    rankGroup.add(bm11);
	    rankGroup.add(bm25);
	    
	    configConstraints.anchor = GridBagConstraints.WEST;
		configConstraints.gridx = 0;
		configConstraints.gridy = 0;
		configPanel.add(labelConfigPreprocessing, configConstraints);
		configConstraints.gridx = 1;
		configConstraints.gridy = 0;
		configPanel.add(nlpStemmer, configConstraints);
		configConstraints.gridx = 1;
		configConstraints.gridy = 1;
		configPanel.add(nlpLemmatizer, configConstraints);		
		configConstraints.gridx = 1;
		configConstraints.gridy = 2;
		configPanel.add(group11Stemmer, configConstraints);
		configConstraints.gridx = 2;
		configConstraints.gridy = 0;
		configPanel.add(labelRankingFunctions, configConstraints);
		configConstraints.gridx = 3;
		configConstraints.gridy = 0;
		configPanel.add(bim, configConstraints);
		configConstraints.gridx = 3;
		configConstraints.gridy = 1;
		configPanel.add(twoP, configConstraints);		
		configConstraints.gridx = 3;
		configConstraints.gridy = 2;
		configPanel.add(bm11, configConstraints);
		configConstraints.gridx = 3;
		configConstraints.gridy = 3;
		configPanel.add(bm25, configConstraints);
		
		configPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Config Panel"));
		getContentPane().add(configPanel);

		pack();
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		// set look and feel to the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new UserInterface().setVisible(true);
			}
		});
	}
}