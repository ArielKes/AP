package game_src;

import java.util.HashMap;

public class DictionaryManager {
    //library get Dictionaries as a value and their names as a key
    private HashMap<String, Dictionary>  library;

    public DictionaryManager(){
        library = new HashMap<String, Dictionary>();
    }

    public static DictionaryManager get() {
        return new DictionaryManager();
    }

    public void closeLibrary(){
        //close all the unclosed threads from the searcher in the Dictionary class
        for(Dictionary dict: library.values()) {
            dict.close();
        }
    }

    private boolean dictionaryHandler(char command, String...args) {
        //get the question from the args
        String question = args[args.length - 1];

        //check all fo the args (books names), if we do not have this book name in the library, make one
        for (int i = 0;i < args.length - 1;i++){
            //Check if books is in library
            if(!library.containsKey(args[i]))
                library.put(args[i],new Dictionary(args[i]));

            //go over all the dictionaries in the library and
            //if the word is in one of them return true

            //if command is C
            if(command =='C'){
                return library.get(args[i]).challenge(question);}
            //if command is 'Q'
            if(command =='Q' && library.get(args[i]).challenge(question))
                return true;
        }
        return false;
    }

    public boolean query(String...args) { return dictionaryHandler('Q', args); }

    public boolean challenge(String...args) { return dictionaryHandler('C', args); }

    public int getSize() { return library.size(); }
}
