package test;

import java.util.HashMap;
import java.util.function.Function;

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

    public boolean query(String...args) {
        //get the question from the args
        String question = args[args.length - 1];

        //check all fo the args (books names), if we do not have this book name in the library, make one
        for (int i = 0;i < args.length - 1;i++){
            //Check if books is in library
            if(!library.containsKey(args[i]))
                library.put(args[i],new Dictionary(args[i]));
            //go over all the dictionaries in the library and
            //if the word is in one of them return true
            if(library.get(args[i]).query(question))
                return true;
        }
        return false;
    }

    public boolean challenge(String...args) {
        //get the question from the args
        String question = args[args.length - 1];

        //check all fo the args (books names), if we do not have this book name in the library, make one
        for (int i = 0;i < args.length - 1;i++){
            //Check if books is in library
            if(!library.containsKey(args[i]))
                library.put(args[i],new Dictionary(args[i]));
            //go over all the dictionaries in the library and
            //if the word is in one of them return true
            if(library.get(args[i]).challenge(question))
                return true;
        }
        return false;
    }

    public int getSize() { return library.size(); }
}
