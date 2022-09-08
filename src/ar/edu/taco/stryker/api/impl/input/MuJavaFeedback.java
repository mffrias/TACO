package ar.edu.taco.stryker.api.impl.input;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mujava.api.MutantsInformationHolder;
import mujava.api.Mutation;
import mujava.app.Mutator;

import com.google.common.collect.Maps;

public class MuJavaFeedback {

    /*
     *  -Vector de cantidad de mutaciones aplicadas por línea. Es la clase de equivalencia de ese input.
     *  -Vector de índice de mutaciones aplicado por línea en este input
     *  -Matriz (vector de vectores) de mutadores por línea para ese input
     *  -Feedback Line Number
     */
    
    private Integer skipUntilMutID = null;
    private Integer[] lineMutationIndexes;
    private List<Integer> lastMutatedLines;
    private Mutation[][] lineMutatorsList;
    private int fatherIndex;
    private MutantsInformationHolder mutantsInformationHolder;
    private Mutator mut;
    private List<Integer> mutableLines = null;
    private List<Integer> curMutableLines = null;
    private boolean fatherable;
    private boolean mutateRight;
    private boolean getSibling = true;
    private boolean UNSAT = false;
    private boolean stop = false;
    private int methodFirstLine;
    private Map<Integer, Set<Integer>> nonCompilableIndexes = Maps.newHashMap();
    private String unskippableOJML4CFilename;
    private String unskippableOJML4CPackage;

    public MuJavaFeedback(int methodFirstLine, Integer[] lineMutationIndexes, Mutation[][] lineMutatorsList, 
            List<Integer> lastMutatedLines, List<Integer> mutableLines, List<Integer> curMutableLines) {
        super();
        if (lineMutationIndexes.length > lineMutatorsList.length) {
            System.out.println("PROBLEMMMM");
        }
        this.methodFirstLine = methodFirstLine;
        this.lineMutationIndexes = lineMutationIndexes;
        this.lineMutatorsList = lineMutatorsList;
        this.fatherable = true;
        this.lastMutatedLines = lastMutatedLines;
        this.mutableLines = mutableLines;
        this.curMutableLines = curMutableLines;
        for (int i = 0 ; i < lineMutationIndexes.length; ++i) {
            nonCompilableIndexes.put(i, new HashSet<Integer>());
        }
    }
    
    public List<Integer> getCurMutableLines() {
        return curMutableLines;
    }
    
    public Map<Integer, Set<Integer>> getNonCompilableIndexes() {
        return nonCompilableIndexes;
    }
    
    public int getMethodFirstLine() {
        return methodFirstLine;
    }

    public List<Integer> getMutableLines() {
        return mutableLines;
    }
    
    public Integer getSkipUntilMutID() {
        return skipUntilMutID;
    }
    
    public void setSkipUntilMutID(Integer skipUntilMutID) {
        this.skipUntilMutID = skipUntilMutID;
    }
    
    public Integer[] getLineMutationIndexes() {
        return lineMutationIndexes;
    }
    
    public Mutation[][] getLineMutatorsList() {
        return lineMutatorsList;
    }
    
    public void setLineMutationIndexes(Integer[] lineMutationIndexes) {
        this.lineMutationIndexes = lineMutationIndexes;
    }
    
    public void setLineMutatorsList(Mutation[][] lineMutatorsList) {
        this.lineMutatorsList = lineMutatorsList;
    }
    
    public boolean isUNSAT() {
        return UNSAT;
    }
    
    public void setUNSAT(boolean uNSAT) {
        UNSAT = uNSAT;
    }
    
    public int getFatherIndex() {
        return fatherIndex;
    }
    
    public void setFatherIndex(int fatherIndex) {
        this.fatherIndex = fatherIndex;
    }
    
    public Mutator getMut() {
        return mut;
    }
    
    public void setMut(Mutator mut) {
        this.mut = mut;
    }
    
    public MutantsInformationHolder getMutantsInformationHolder() {
        return mutantsInformationHolder;
    }
    
    public void setMutantsInformationHolder(MutantsInformationHolder mutantsInformationHolder) {
        this.mutantsInformationHolder = mutantsInformationHolder;
    }

    public boolean isFatherable() {
        return fatherable;
    }
    
    public void setFatherable(boolean fatherable) {
        this.fatherable = fatherable;
    }
    
    public List<Integer> getLastMutatedLines() {
        return lastMutatedLines;
    }
    
    public void setLastMutatedLines(List<Integer> lastMutatedLines) {
        this.lastMutatedLines = lastMutatedLines;
    }
    
    public boolean isMutateRight() {
        return mutateRight;
    }
    
    public void setMutateRight(boolean mutateRight) {
        this.mutateRight = mutateRight;
    }
    
    public boolean isGetSibling() {
        return getSibling;
    }
    
    public void setGetSibling(boolean getSibling) {
        this.getSibling = getSibling;
    }

    public boolean isStop() {
        return stop;
    }
    
    public void setStop(boolean stop) {
        this.stop = stop;
    }
    
    public String getUnskippableOJML4CFilename() {
        return unskippableOJML4CFilename;
    }
    
    public void setUnskippableOJML4CFilename(String unskippableOJML4CFilename) {
        this.unskippableOJML4CFilename = unskippableOJML4CFilename;
    }
    
    public String getUnskippableOJML4CPackage() {
        return unskippableOJML4CPackage;
    }
    
    public void setUnskippableOJML4CPackage(String unskippableOJML4CPackage) {
        this.unskippableOJML4CPackage = unskippableOJML4CPackage;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fatherIndex;
        result = prime * result + ((lastMutatedLines == null) ? 0 : lastMutatedLines.hashCode());
        result = prime * result + Arrays.hashCode(lineMutationIndexes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MuJavaFeedback other = (MuJavaFeedback) obj;
        if (fatherIndex != other.fatherIndex) return false;
        if (lastMutatedLines == null) {
            if (other.lastMutatedLines != null) return false;
        } else if (!lastMutatedLines.equals(other.lastMutatedLines)) return false;
        if (!Arrays.equals(lineMutationIndexes, other.lineMutationIndexes)) return false;
        return true;
    }

}
