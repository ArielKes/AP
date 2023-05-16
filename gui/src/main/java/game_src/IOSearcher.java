package game_src;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


interface FileSearcher {
	public boolean search(String word, String...fileNames);
	public void stop();

}

public class IOSearcher implements FileSearcher{

	boolean stopMe;
	
	public IOSearcher() {
		stopMe=false;
	}
	
	public boolean search(String word, String...fileNames){
		boolean found=false;
		try {
			for(int i=0;!stopMe && i<fileNames.length && !found; i++) {
				Scanner s=new Scanner(new File(fileNames[i]));
				while(s.hasNext() && !found && !stopMe)
					if(s.next().equals(word))
						found=true;
				s.close();
			}
		}catch(Exception e) {}
		
		return found;
	}
	
	public void stop() {
		stopMe=true;
	}
}

class ParIOSearcher implements FileSearcher{

	ExecutorService es;

	public ParIOSearcher() {
		es= Executors.newCachedThreadPool();
	}

	public boolean search(String word, String...fileNames){
		ArrayList<IOSearcher> searchers=new ArrayList<>();
		ArrayList<Future<Boolean>> fs=new ArrayList<>();

		for(String fn : fileNames) {
			IOSearcher s = new IOSearcher();
			searchers.add(s);
			fs.add(es.submit(()->{
				boolean found = s.search(word, fn);
				if(found) {
					searchers.forEach(srch->srch.stop());
				}
				return found;
			}));
		}

		boolean found = false;
		for(Future<Boolean> f : fs) {
			try {
				found|=f.get();
			} catch (InterruptedException | ExecutionException e) {}
		}

		return found;
	}

	public void stop() {
		es.shutdownNow();
	}

	@Override
	public void finalize() {
		es.shutdown();
	}


}
