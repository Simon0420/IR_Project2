import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class UserInterface extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel labelQuery = new JLabel("Query: ");
	private JTextField textQuery = new JTextField(40);
	private JButton searchButton = new JButton("Search");
	private JButton deleteButton = new JButton("Delete");
	private JLabel labelConfigPreprocessing = new JLabel("Pre-processing: ");
	private JButton readButton = new JButton("Read Doc-Collection");
	private JLabel labelRankingFunctions = new JLabel("Ranking-f(x)s: ");
	private ActionListener buttonListener = new IR_UI_Listener();
	private ButtonGroup rankGroup = new ButtonGroup();
	private static JTextPane textPane;
	private JTextArea textArea;

	public UserInterface() {
		super("Information Retrieval System");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		// CONFIG PANEL
		JPanel preproPanel = new JPanel(new GridBagLayout());
		GridBagConstraints preproConstraints = new GridBagConstraints();
		preproConstraints.insets = new Insets(5, 5, 5, 5);

		JRadioButton nlpStemmer = new JRadioButton("CoreNLP Stemmer");
		nlpStemmer.setActionCommand("nlpStemmer");
		nlpStemmer.addActionListener(buttonListener);
		JRadioButton nlpLemmatizer = new JRadioButton("CoreNLP Lemmatizer");
		nlpLemmatizer.addActionListener(buttonListener);
		nlpLemmatizer.setActionCommand("nlpLemmatizer");
		JRadioButton group11Stemmer = new JRadioButton("Group#11 Stemmer");
		group11Stemmer.addActionListener(buttonListener);
		group11Stemmer.setActionCommand("group11Stemmer");
		if (Preprocessing.isNlpLemma()) {
			nlpLemmatizer.setSelected(true);
		} else if (Preprocessing.isNlpStemmer()) {
			nlpStemmer.setSelected(true);
		} else {
			group11Stemmer.setSelected(true);
		}

		// Group the radio buttons.
		ButtonGroup ppGroup = new ButtonGroup();
		ppGroup.add(nlpStemmer);
		ppGroup.add(nlpLemmatizer);
		ppGroup.add(group11Stemmer);

		preproConstraints.anchor = GridBagConstraints.WEST;
		preproConstraints.gridx = 0;
		preproConstraints.gridy = 0;
		preproPanel.add(labelConfigPreprocessing, preproConstraints);
		preproConstraints.gridx = 1;
		preproConstraints.gridy = 0;
		preproPanel.add(nlpStemmer, preproConstraints);
		preproConstraints.gridx = 1;
		preproConstraints.gridy = 1;
		preproPanel.add(nlpLemmatizer, preproConstraints);
		preproConstraints.gridx = 1;
		preproConstraints.gridy = 2;
		preproPanel.add(group11Stemmer, preproConstraints);
		preproConstraints.gridx = 1;
		preproConstraints.gridy = 3;
		preproPanel.add(readButton, preproConstraints);
		preproPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Pre-Processing Panel"));
		getContentPane().add(preproPanel);

		readButton.setActionCommand("readDocs");
		readButton.addActionListener(buttonListener);

		// QUERY PANEL
		JPanel queryPanel = new JPanel(new GridBagLayout());
		GridBagConstraints queryConstraints = new GridBagConstraints();
		searchButton.setActionCommand("search");
		deleteButton.setActionCommand("delete");
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
		queryPanel.add(searchButton, queryConstraints);
		queryConstraints.gridx = 2;
		queryConstraints.gridy = 1;
		queryPanel.add(deleteButton, queryConstraints);
		queryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Query Panel"));
		getContentPane().add(queryPanel);
		searchButton.addActionListener(buttonListener);
		deleteButton.addActionListener(buttonListener);
		

		// CONFIG PANEL
		JPanel configPanel = new JPanel(new GridBagLayout());
		GridBagConstraints configConstraints = new GridBagConstraints();
		configConstraints.insets = new Insets(5, 5, 5, 5);

		JRadioButton bim = new JRadioButton("BIM");
		bim.setActionCommand("bim");
		bim.setSelected(true);
		JRadioButton twoP = new JRadioButton("2-P");
		twoP.setActionCommand("twoP");
		JRadioButton bm11 = new JRadioButton("BM11");
		bm11.setActionCommand("bm11");
		JRadioButton bm25 = new JRadioButton("BM25");
		bm25.setActionCommand("bm25");
		JRadioButton lm = new JRadioButton("LM");
		lm.setActionCommand("lm");
		// Group the radio buttons.
		rankGroup.add(bim);
		rankGroup.add(twoP);
		rankGroup.add(bm11);
		rankGroup.add(bm25);
		rankGroup.add(lm);

		configConstraints.anchor = GridBagConstraints.WEST;
		configConstraints.gridx = 1;
		configConstraints.gridy = 0;
		configPanel.add(labelRankingFunctions, configConstraints);
		configConstraints.gridx = 2;
		configConstraints.gridy = 0;
		configPanel.add(bim, configConstraints);
		configConstraints.gridx = 2;
		configConstraints.gridy = 1;
		configPanel.add(twoP, configConstraints);
		configConstraints.gridx = 2;
		configConstraints.gridy = 2;
		configPanel.add(bm11, configConstraints);
		configConstraints.gridx = 2;
		configConstraints.gridy = 3;
		configPanel.add(bm25, configConstraints);
		configConstraints.gridx = 2;
		configConstraints.gridy = 4;
		configPanel.add(lm, configConstraints);

		configPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Config Panel"));
		getContentPane().add(configPanel);

		// Console PANEL
		textPane = new JTextPane();
		textArea = new JTextArea("test");
		textPane.setEditable(false);
		textPane.setSize(300, 100);
		textPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Console Panel"));
		textPane.add(textArea);
		getContentPane().add(textPane);

		pack();
		setLocationRelativeTo(null);
	}

	class IR_UI_Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			switch (command) {
			case "readDocs":
				Preprocessing.run();
				searchButton.setEnabled(true);
				break;
			case "nlpStemmer":
				Preprocessing.enableNlpStemmer();
				break;
			case "nlpLemmatizer":
				Preprocessing.enableNlpLemma();
				break;
			case "group11Stemmer":
				Preprocessing.enableOwnStemmer();
				break;
			case "delete":
				textQuery.setText("");
				break;
			case "search":
				searchButton.setEnabled(false);
				readButton.setEnabled(false);
				String cmd = rankGroup.getSelection().getActionCommand();
				System.out.println(cmd);
				Query q = new Query(textQuery.getText(), cmd, 10);
				System.out.println(q.terms);
				q.search();
				printResults(q);
				System.out.println(q.sortedResults);
				searchButton.setEnabled(true);
				readButton.setEnabled(true);
			}

		}
	}

	public void printResults(Query q){
		int[] docs = q.getTopDocs();
		String res = "Query: " + q.fullQuery+"\n";
		res = res + "Rank |\tDocument ID\n";
		for(int i=0; i<docs.length; i++){
			res = res + (i+1) + "\t" + docs[i] + "\n";
		}
		textPane.setText(res);
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