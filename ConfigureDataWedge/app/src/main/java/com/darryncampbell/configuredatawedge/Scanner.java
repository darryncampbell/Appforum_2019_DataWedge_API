package com.darryncampbell.configuredatawedge;

//  Class to describe an attached scanner
public class Scanner {
    public Scanner(String name, String id, int index, Boolean state)
    {
        this.mName = name;
        this.mId = id;
        this.mIndex = index;
        this.mState = state;
    }
    public String getName() {return mName;}
    public String getId() {return mId;}
    public int getIndex() {return mIndex;}
    public Boolean getState() {return mState;}
    private String mName;
    private String mId;
    private int mIndex;
    private Boolean mState;
}
