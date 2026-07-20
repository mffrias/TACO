package edu.utep.ds2;

public class PaperCode {


    /*@ requires true;
        ensures true;
     @*/
    public static float calculateRisk(int age, int incidents) {
        if (age < 16 || age > 60) {
            return -1;
        }
        int score = age / 2;
        int avg = 41;
        score = score + incidents * 7;
        score = score - 5;
        int factor = 5 * incidents + 1;
        float penalty = 1.5f;
        int adjustment = 0;
        boolean risky = false;
        if (score > 80) {
            factor = factor - avg;
            score = score - 5;
            if (score < 150) {
                score = 0;//(int) (score - avg * penalty);
            } else {
                score = 150;
            }
            adjustment = score / factor;
            risky = adjustment > 10;
        } else {
            score = score + 7;
            factor = factor - 1;
            adjustment = score / 2;
            risky = adjustment < 5;
        }
        float finalScore = adjustment + score * 2 +
                factor;
        if (risky) {
            finalScore += 2.5;
        } else {
            finalScore -= 1.2 * penalty;
        }
        return finalScore;
    }

}
