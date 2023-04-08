package test;

import java.util.HashMap;

public class DictionaryManager {
    //Dictionary dict;
    HashMap<String, Dictionary>  library;

    public DictionaryManager(){
        library = new HashMap<String, Dictionary>();
    }

    public static DictionaryManager get() {
        return new DictionaryManager();
    }

    public boolean query(String...args) {
        String question = args[args.length - 1];

        for (int i = 0;i < args.length - 1;i++){
            //Check if books is in library
            if(!library.containsKey(args[i]))
                library.put(args[i],new Dictionary(args[i]));
            if(library.get(args[i]).query(question))
                return true;
        }
        return false;
    }

    public boolean challenge(String...args) {
        String question = args[args.length - 1];

        for (int i = 0;i < args.length - 1;i++){
            //Check if books is in library
            if(!library.containsKey(args[i]))
                library.put(args[i],new Dictionary(args[i]));
            if(library.get(args[i]).challenge(question))
                return true;
        }
        return false;
    }

    public int getSize() {
        return library.size();
    }
}
