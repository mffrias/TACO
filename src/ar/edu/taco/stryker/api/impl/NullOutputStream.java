package ar.edu.taco.stryker.api.impl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link OutputStream} that discards everything written to it. DarwinistController passes it as the
 * output and error streams of {@code JavaCompiler.run} to silence javac when it compiles candidates.
 */
public class NullOutputStream extends OutputStream {
	  /** Discards {@code b}. */
	  @Override
	  public void write(int b) throws IOException {
	  }
	}
