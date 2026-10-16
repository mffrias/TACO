package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import ar.edu.taco.stryker.api.impl.MuJavaController.MsgDigest;
import ar.edu.taco.utils.FileUtils;

/**
 * Process-wide cache of TACO verdicts for variabilized methods, used by
 * {@code DarwinistController.variablize} to skip TACO when a variabilized file with the same
 * content has already been analysed. Mutants that differ only in statements that end up
 * variabilized produce identical variabilized files, which is what makes the cache useful.
 *
 * <p><b>Key:</b> the MD5 of the file content after deleting, on every line, the text from
 * {@code //mutGenLimit} to the end of the line, so mutation-budget and {@code mutID} annotations
 * do not affect it. <b>Value:</b> {@code true} if TACO answered SAT. Only the first verdict stored
 * for a key is kept.
 *
 * <p><b>Consequences:</b> the key ignores the failing input that TACO was fixed to, as well as the
 * TACO configuration (e.g. scopes), so a cached verdict may have been computed for a different
 * counterexample. Reusing an UNSAT verdict still appears to justify pruning (any fix must also
 * pass that other input), while reusing a SAT verdict can only stop variabilization earlier than
 * a fresh check would. Entries are never removed, so the cache lives as long as the JVM, across
 * Stryker runs in the same process. Every call re-reads and re-hashes the file.
 *
 * <p>Not thread-safe (unsynchronized lazy singleton over a {@link HashMap}); it is only used from
 * the synchronized {@code DarwinistController.computateFeedback}.
 */
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
	
	/**
	 * Stores {@code verdict} ({@code true} for SAT) for the content of {@code file}, unless a
	 * verdict for that content is already stored.
	 */
	public void put(File file, boolean verdict) {
		MsgDigest digest = getDigest(file);
		if (!verdicts.containsKey(digest)) {
			verdicts.put(digest, verdict);
		}
	}

	/**
	 * @return the verdict stored for the content of {@code file} ({@code true} for SAT); throws
	 *         {@link NullPointerException} if there is none, so check {@link #containsFile(File)}
	 *         first
	 */
	public boolean get(File file) {
		MsgDigest digest = getDigest(file);
		return verdicts.get(digest);
	}
	
	/** @return whether a verdict is stored for the content of {@code file} */
	public boolean containsFile(File file){
		MsgDigest digest = getDigest(file);
		return verdicts.containsKey(digest);
	}
	
	/**
	 * MD5 of the file content with every {@code //mutGenLimit} line suffix removed. The content is
	 * written through a {@link DigestOutputStream} to a temporary file that is deleted right away;
	 * only the digest is kept. Uses the platform default charset.
	 *
	 * @throws IllegalArgumentException if the file cannot be read or hashed
	 */
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
            duplicatesTempFile.delete();
        }
        catch (Exception e) {
        	throw new IllegalArgumentException("exception thrown while trying to compute digest in class VariablizedSATVerdicts");
        }
    	byte[] digest = dos.getMessageDigest().digest();
        return new MsgDigest(digest);		
	}
		
	
}
