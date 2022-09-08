package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import ar.edu.taco.stryker.api.impl.MuJavaController.MsgDigest;
import ar.edu.taco.utils.FileUtils;

public class VariablizedSATVerdicts {
	
	// implemented as a singleton for convenience
	private static VariablizedSATVerdicts instance = null;
	
	private Map<MsgDigest,Boolean> verdicts;
	
	private VariablizedSATVerdicts() {
		verdicts = new HashMap<MsgDigest,Boolean>();
	}
	
	public static VariablizedSATVerdicts getInstance() {
		if (instance ==null) {
			instance = new VariablizedSATVerdicts();
		}
		return instance;
	}
	
	public void put(File file, boolean verdict) {
		MsgDigest digest = getDigest(file);
		if (!verdicts.containsKey(digest)) {
			verdicts.put(digest, verdict);
		}
	}

	public boolean get(File file) {
		MsgDigest digest = getDigest(file);
		return verdicts.get(digest);
	}
	
	public boolean containsFile(File file){
		MsgDigest digest = getDigest(file);
		return verdicts.containsKey(digest);
	}
	
	private static MsgDigest getDigest(File file) {
        DigestOutputStream dos;
        File duplicatesTempFile = null;
        String content = null;
        try {
        	content = FileUtils.readFile(file.getAbsolutePath());
        }
        catch (Exception e) {
        	throw new IllegalArgumentException("invalid or null file");
        }
        String tunedContent = "";
        String lines[] = content.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i];
            if (line.contains("//mutGenLimit")) {
                tunedContent += line.substring(0, line.indexOf("//mutGenLimit")) + "\n";
            } else {
                tunedContent += line + "\n";
            }
        }
        try {
        	duplicatesTempFile = File.createTempFile("forDuplicatesAgain", null);
        	dos = new DigestOutputStream(new FileOutputStream(duplicatesTempFile, false), MessageDigest.getInstance("MD5"));
        	dos.write(tunedContent.getBytes());
        	dos.flush();
        	dos.close();
        }
        catch (Exception e) {
        	throw new IllegalArgumentException("exception thrown while trying to compute digest in class VariablizedSATVerdicts");
        }
    	byte[] digest = dos.getMessageDigest().digest();
        return new MsgDigest(digest);		
	}
		
	
}
