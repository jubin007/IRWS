import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
//here we are importing diffrent liberies of java to run the program.

class VecSpcMdl {

    ArrayList stopWords = new ArrayList();
    BufferedReader b1,b2;
    /* This is how to declare HashMap */
    HashMap<String,String> hmDocumentTitles = new HashMap<>();
    HashMap<String,Integer> hmTotalOccurenceq = new HashMap<>();
    HashMap<String,HashMap<String, Float>>termFreq = new HashMap<>(), tfidf = new HashMap<>();
    HashMap<String,HashMap<String, Integer>>occurences = new HashMap<>();
    HashMap<String,Float> idf = new HashMap<>(), total_tfidf = new HashMap<>();
	StringBuilder st = new StringBuilder();
     HashMap<String,HashMap<String, Integer>> doc = new HashMap<>();
     ArrayList<String> wrdCntnr = new ArrayList<>();
		String document,doccID = "",doccTitle = "";
        porterStem stem = new porterStem();

    public void startProcessing() throws IOException {
        String stopWordLine;
        int startngPoint = 0;
            b1=new BufferedReader(new FileReader(new File("stopWord.txt")));
            b2=new BufferedReader(new FileReader(new File("IR1.txt")));

        while((stopWordLine=b1.readLine())!=null ){
            stopWords.add(stopWordLine);
        }

	// here we are accesing the stemmer for stemming
	   while ((stopWordLine = b2.readLine()) != null) {
		if (startngPoint == 0) {
			doccID = stopWordLine.replace("Document", "").trim();
		//checking for Seperation of Documents
			} else if(stopWordLine.equals("********************************************")) {
				hmDocumentTitles.put(doccID, doccTitle);       /*Adding elements to HashMap*/
				document = st.toString().trim();
				st = new StringBuilder();
				 HashMap<String, Integer> eachDocWordCount = new HashMap<>();

                    // Regular Expressions are used to remove the punctuation and converting them into LowerCase
				String[] words = document.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
				char[] chars;
				int count;
				String stdWord;

				for (String word : words ) {
					 if (!stopWords.contains(word) && !word.isEmpty()) {

             chars = word.toCharArray();
						 stem.add(chars, chars.length);
						 stem.stem();
						 stdWord = stem.toString();

						 if (eachDocWordCount.containsKey(word)) {
							 eachDocWordCount.put(word, eachDocWordCount.get(word)+1);
						 } else {
							 eachDocWordCount.put(stdWord, 1);
						 }

						 if (!wrdCntnr.contains(stdWord)) {
							 wrdCntnr.add(stdWord);
						 }

					 }

				}

        occurences.put(doccID, eachDocWordCount);
				doc.put(doccID, eachDocWordCount);

				int total = 0;
				for (String word : eachDocWordCount.keySet()) {
					total += eachDocWordCount.get(word);
				}
				hmTotalOccurenceq.put(doccID, total);

				HashMap<String,Float> tf = new HashMap<>();

				for (String word: wrdCntnr) {
					tf.put(word, (float) eachDocWordCount.getOrDefault(word, 0) / total);       /* Get values based on key*/
				}
				termFreq.put(doccID, tf);

				startngPoint = 0;
				continue;

			} else if(!stopWordLine.trim().isEmpty()) {

				st.append(stopWordLine).append(" ");

			} else {

				doccTitle = st.toString().trim();
				st = new StringBuilder();

			}

			startngPoint++;
		}

		// write idf to file

    FileWriter FileStemDoc = new FileWriter("TermFrequency.txt");
		FileWriter FileIdfs = new FileWriter("IDF.txt");

		int totalWords;
		for (String word : wrdCntnr) {
			totalWords = 0;
			FileStemDoc.write(word + " Occurance ");
			for (String doccID : doc.keySet()) {
				totalWords += doc.get(doccID).getOrDefault(word, 0);
				FileStemDoc.write("Document_No" + doccID + "\t ");
                                FileStemDoc.write(" \n");

			}
			FileStemDoc.write("\n\n");

			FileIdfs.write(word + ":" + totalWords + "\n");
			idf.put(word, (float) Math.log10(termFreq.size() / totalWords));
		}
					System.out.println("TF and IDF Calculation is completed");

		FileIdfs.close();
		FileStemDoc.close();

		float totalTerm, totalDoc;
    FileWriter tfidfs = new FileWriter("TF-IDF.txt");
         String seperator = "::::"; //here we are seprating strings
         HashMap<String,Float> wrdTf;

		for (String doccID : doc.keySet()) {
			System.out.println("Processing Document.. "+doccID);
			tfidfs.write(doccID + seperator + hmDocumentTitles.get(doccID) + seperator);

      totalDoc = 0;
			totalTerm = 0;
			wrdTf = termFreq.get(doccID);

			for (String word : wrdCntnr) {

				if (wrdTf.containsKey(word)) {
					totalTerm =	wrdTf.get(word) * idf.get(word);
					totalDoc += Math.pow(totalTerm, 2);
				} else {
					totalTerm = 0;
				}

				tfidfs.write(word +"="+ totalTerm + ",");

			}

			tfidfs.write(seperator + Math.sqrt(totalDoc) + "\n");

		}
			tfidfs.close();
		}

	}
