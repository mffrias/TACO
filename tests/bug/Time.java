package bug;

public class Time {
  public int second;
  public int minute;
  public int hour;

  //@ invariant 0 <= second && second < 60;
  //@ invariant 0 <= minute && minute < 60;
  //@ invariant 0 <= hour && hour < 24;

  //Time-int,int,int-
  //@ requires 0 <= h && h < 24;
  //@ requires 0 <= m && m < 60;
  //@ requires 0 <= s && s < 60;
  //@ ensures hour == h;
  //@ ensures minute == m;
  //@ ensures second == s;
  public Time(int h, int m, int s) {
    hour = h;
    minute = m;
    second = s;
  }

  //setSecond-int-
  //@ ensures 0 <= s && s < 60 ==> second == s;
  //@ signals (IllegalArgumentException e) s < 0 || 60 <= s;
  public void setSecond(int s) {
    if (s < 0 || 60 <= s) {
      throw new IllegalArgumentException();
    } else {
      this.second = s;
    }
  }

  //setMinute-int-
  //@ ensures 0 <= m && m < 60 ==> minute == m;
  //@ signals (IllegalArgumentException e) m < 0 || 60 <= m;
  public void setMinute(int m) {
    if (m < 0 || 60 <= m) {
      throw new IllegalArgumentException();
    } else {
      this.minute = m;
    }
  }

  //setHour-int-
  //@ ensures 0 <= h && h < 24 ==> hour == h;
  //@ signals (IllegalArgumentException e) h < 0 || 24 <= h;
  public void setHour(int h) {
    if (h < 0 || 24 <= h) {
      throw new IllegalArgumentException();
    } else {
      this.hour = h;
    }
  }

  //getTime--
  //@ ensures second == \result.second && minute == \result.minute && hour == \result.hour;
  public Time getTime() {
    Time t = new Time(this.hour, this.minute, this.second);
    return t;
  }

  //getSecond--
  //@ ensures \result == second;
  public int getSecond() {
    return second;
  }

  //getMinute--
  //@ ensures \result == minute;
  public int getMinute() {
    return minute;
  }

  //getHour--
  //@ ensures \result == hour;
  public int getHour() {
    return hour;
  }

  //convertToSeconds--
  //@ ensures \result == hour*60*60 + minute*60 + second;
  public int convertToSeconds() {
    return (hour * 60 * 60 + minute * 60 + second);
  }

  //decr--
  //@ ensures hour*60*60 + minute*60 + second == 0 ==> hour*60*60 + minute*60 + second == 0;
  //@ ensures hour*60*60 + minute*60 + second > 0 ==> hour*60*60 + minute*60 + second == \old(hour)*60*60 + \old(minute)*60 + \old(second) - 1;
  public void decr() {
    if (isTimeZero())
      return;
    else {
      second--;
      if (second < 0) {
        second = 59;
        minute--;
        if (minute < 0) {
          minute = 59;
          hour--;
        }
      }
    }
  }

  //timer--
  //@ ensures hour*60*60 + minute*60 + second == 0;
  public void timer() {
    while (!isTimeZero()) {
      // each time around this loop should take 1 second, ideally
      decr();
    }
  }

  //timerp-int,int,int-
  //@ requires 0 <= h && h < 24;
  //@ requires 0 <= m && m < 60;
  //@ requires 0 <= s && s < 60;
  //@ ensures hour*60*60 + minute*60 + second == 0;
  public void timerp(int h, int m, int s) {
    setHour(h);
    setMinute(m);
    setSecond(s);
    timer();
  }

  //isTimeZero--
  //@ ensures \result == true <==> hour*60*60 + minute*60 + second == 0;
  public boolean isTimeZero() {
    return (convertToSeconds() == 0);
  }

  //reset--
  //@ ensures second == 0 && minute == 0 && hour == 0;
  public void reset() {
    second = 0;
    minute = 0;
    hour = 0;
  }

  //later_than-Time-
  //+MORE@ requires hour != 10 || minute != 36 || second != 4 || start.hour != 4 || start.minute != 36 || start.second != 48;
  //@ requires 0 <= start.second && start.second < 60;
  //@ requires 0 <= start.minute && start.minute < 60;
  //@ requires 0 <= start.hour && start.hour < 24;
  //@ ensures \result == true <==> ((hour > start.hour) || (hour == start.hour && minute > start.minute) || (hour == start.hour && minute == start.minute && second > start.second));
  public boolean later_than(Time start) {
    if (this.hour != start.hour) {
     // timerp(start.hour, start.minute, start.second); // ESTE ES EL INTENTO DE LA APRT
      return this.hour > start.hour; // return this.hour > start.hour; ESTE ES EL FIX
    } else if (this.minute != start.minute) {
      return this.minute > start.minute;
    } else {
      return this.second > start.second;
    }
  }

  //equals-Time-
  //@ requires 0 <= t.second && t.second < 60;
  //@ requires 0 <= t.minute && t.minute < 60;
  //@ requires 0 <= t.hour && t.hour < 24;
  //@ ensures \result == true <==> hour == t.hour && minute == t.minute && second == t.second;
  public boolean equals(Time t) {
    return this.hour == t.hour && this.minute == t.minute && this.second == t.second;
  }

//@ requires 4 == stop.hour;
  //@ requires 0 == stop.minute;
  //@ requires 15 == stop.second;
  //@ requires 0 == start.hour;
  //@ requires 37 == start.minute;
  //@ requires 15 == start.second;
  //@ ensures 0 == \result.second;
  public Time trustedDifference(Time start, Time stop) {
    Time diff = new Time(23, 59, 59);
    int temp_second = stop.getSecond();
    int temp_minute = stop.getMinute();
    int temp_hour = stop.getHour();

    if (temp_second < start.getSecond()) {
      --temp_minute;
      temp_second += 60;
    }

    diff.second = temp_second - start.getSecond();

    if (temp_minute < start.getMinute()) {
      --temp_hour;
      temp_minute += 60;
    }

    diff.minute = temp_minute - start.getMinute();
    diff.hour = temp_hour - start.getHour();
    return (diff);
  }
  
  
  //@ requires 4 == stop.hour;
  //@ requires 0 == stop.minute;
  //@ requires 15 == stop.second;
  //@ requires 0 == start.hour;
  //@ requires 37 == start.minute;
  //@ requires 15 == start.second;
  //@ ensures 0 == \result.second;
  public Time difference(Time start, Time stop) {
    return trustedDifference(start, stop);
  }

  //@ requires 0 <= start.second && start.second < 60;
  //@ requires 0 <= start.minute && start.minute < 60;
  //@ requires 0 <= start.hour && start.hour < 24;
  //@ requires 0 <= stop.second && stop.second < 60;
  //@ requires 0 <= stop.minute && stop.minute < 60;
  //@ requires 0 <= stop.hour && stop.hour < 24;
  //@ requires 0 <= sel && sel < 5;
  //@ ensures 0 <= sel && sel <= 2 ==> \result.hour == 0 && \result.minute == 0 && \result.second == 0;
  //@ ensures sel == 3 && (start.second != stop.second || start.minute != stop.minute || start.hour != stop.hour) ==> \result.hour == \old(hour) && \result.minute == \old(minute) && \result.second == \old(second);
  //@ ensures sel == 3 && (start.second == stop.second && start.minute == stop.minute && start.hour == stop.hour) ==> \result.hour == 0 && \result.minute == 0 && \result.second == 0 && start.hour == 0 && start.minute == 0 && start.second == 0 && stop == \old(stop);
  //@ ensures sel == 4 && ((stop.hour > start.hour) || (stop.hour == start.hour && stop.minute > start.minute) || (stop.hour == start.hour && stop.minute == start.minute && stop.second > start.second)) ==> (((((stop.second < start.second) ? (stop.minute -1): stop.minute) < start.minute) ? (stop.hour -1): stop.hour) - start.hour) == \result.hour;
  //@ ensures sel == 4 && ((stop.hour > start.hour) || (stop.hour == start.hour && stop.minute > start.minute) || (stop.hour == start.hour && stop.minute == start.minute && stop.second > start.second)) ==> ((((stop.second < start.second) ? (stop.minute -1): stop.minute) < start.minute) ? (((stop.second < start.second) ? (stop.minute -1): stop.minute) + 60 - start.minute) : (((stop.second < start.second) ? (stop.minute -1): stop.minute) - start.minute)) == \result.minute;
  //@ ensures sel == 4 && ((stop.hour > start.hour) || (stop.hour == start.hour && stop.minute > start.minute) || (stop.hour == start.hour && stop.minute == start.minute && stop.second > start.second)) ==> ((stop.second < start.second) ? (stop.second + 60 - start.second) : (stop.second - start.second)) == \result.second;
  //@ ensures sel == 4 && (((start.hour > stop.hour) || (start.hour == stop.hour && start.minute > stop.minute) || (start.hour == stop.hour && start.minute == stop.minute && start.second > stop.second)) || (start.hour == stop.hour && start.minute == stop.minute && start.second == stop.second)) ==> (((((start.second < stop.second) ? (start.minute -1): start.minute) < stop.minute) ? (start.hour -1): start.hour) - stop.hour) == \result.hour;
  //@ ensures sel == 4 && (((start.hour > stop.hour) || (start.hour == stop.hour && start.minute > stop.minute) || (start.hour == stop.hour && start.minute == stop.minute && start.second > stop.second)) || (start.hour == stop.hour && start.minute == stop.minute && start.second == stop.second)) ==> ((((start.second < stop.second) ? (start.minute -1): start.minute) < stop.minute) ? (((start.second < stop.second) ? (start.minute -1): start.minute) + 60 - stop.minute) : (((start.second < stop.second) ? (start.minute -1): start.minute) - stop.minute)) == \result.minute;
  //@ ensures sel == 4 && (((start.hour > stop.hour) || (start.hour == stop.hour && start.minute > stop.minute) || (start.hour == stop.hour && start.minute == stop.minute && start.second > stop.second)) || (start.hour == stop.hour && start.minute == stop.minute && start.second == stop.second)) ==> ((start.second < stop.second) ? (start.second + 60 - stop.second) : (start.second - stop.second)) == \result.second;
  public Time timeOptions(Time start, Time stop, int sel) {
    if (sel == 0) {
      reset();
    } else if (sel == 1) {
      timerp(start.hour, start.minute, start.second);
    } else if (sel == 2) {
      timer();
    } else if (sel == 3) {
      if (start.equals(stop)) {
        start.reset();
        return start.getTime();
      }
    } else {
      return difference(start, stop);
    }
    return getTime();
  }
  
  /*@ requires true;
    @ ensures hour != 16;
    @*/
  public void bugWithTrue() {
	  int k = 0;
	  if (true) 
		  return;
  }
}
