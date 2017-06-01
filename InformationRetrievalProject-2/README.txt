Readme IR Project
------------------
The code of our project was written in eclipse.
Therefore, this archive is also a eclipse project structure and could be imported easily. 
Nevertheless, you can also find the Java-code in the src-folder and the compiled java classes are within the bin folder.

NOTE: The coded can be also found in our GitHub repository: https://github.com/Simon0420/IR_Project2

>> How to start the program?
For the simplest way to start the program, you can simply use the Executable Jar File named 'IRSystemApplciation'.
However, if you want to start the program of the project imported within eclipse for example, you need to run the main-method of the UserInterface class.

The classes Evaluation and PortersStemmer also contain main-methods.
With the help of the evaluation class, the evaluation was much simpler than with the final UI. 
The PortersStemmer still contains a main-method due to testing reasons.

>> How to get results?
If the program is started, the UI will show up.
1. Read in the document collection and preprocess it by choosing a morphological normalization method. 
The document will be also indexed simultaneously. (Takes about 40 seconds, depending on the chosen method.)
2. Submit a query by using the textfield in the query panel.
3. Choose a desired ranking function in the "config" panel.
4. Click search.
5. The results are shown in the output panel.
Additionally, the number of results can be adapted within the query panel.

Explanation of other folders/files in the archive:
Folder "20news-bydate": Document-Collection
Files "stopwords" and "stopwords_ranksnl": stopword lists
Jar File: "stanford-corenlp-2017-04-14-build": Stanford CoreNLP API

