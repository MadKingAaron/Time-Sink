package com.example.projecttimesink;

public class ActionableList
{
    private Actionable actionableObject;
    private ActionableList previous;
    private int length;

    public ActionableList() { instantiateStringList(null, null); }

    public ActionableList(Actionable actionableObject) { instantiateStringList(actionableObject, null); }

    public ActionableList(Actionable actionableObject, ActionableList actionableList) { instantiateStringList(actionableObject, actionableList); }

    public ActionableList(Actionable[] actionableArray) { this(actionableArray, null); }

    public ActionableList(Actionable[] actionableArray, ActionableList actionableList)
    {
        if(actionableArray == null)
        {
            System.err.println("ERROR NULL ACTIONABLE OBJECT ARRAY");
            return;
        }

        int actionableObject = 0;

        while(actionableObject < actionableArray.length && actionableArray[actionableObject] == null)
            actionableObject++;

        if(actionableObject == actionableArray.length)
        {
            System.err.println("ERROR NO VALID ACTIONABLE OBJECTS IN ARRAY");
            return;
        }

        this.actionableObject = actionableArray[actionableObject];
        this.previous = actionableList;

        if(this.previous == null)
            this.length = 1;
        else
            this.length = this.previous.length + 1;

        while(++actionableObject < actionableArray.length)
        {
            if(actionableArray[actionableObject] != null)
            {
                this.previous = new ActionableList(this.actionableObject, this.previous);
                this.actionableObject = actionableArray[actionableObject];
                this.length++;
            }
        }
    }

    private void instantiateStringList(Actionable actionableObject, ActionableList actionableList)
    {
        if(actionableObject == null)
        {
            if(actionableList != null)
                System.err.println("ERROR NULL ACTIONABLE OBJECT");

            this.actionableObject = null;
            this.previous = null;
            this.length = 0;

            return;
        }

        this.actionableObject = actionableObject;
        this.previous = actionableList;

        if(this.previous == null)
            this.length = 1;
        else
            this.length = this.previous.length + 1;
    }

    public void add(Actionable actionableObject)
    {
        if(actionableObject == null)
        {
            System.err.println("ERROR NULL ACTIONABLE OBJECT");
            return;
        }

        if(this.actionableObject == null)
        {
            this.actionableObject = actionableObject;
            this.length = 1;
        }
        else
        {
            this.previous = new ActionableList(this.actionableObject, this.previous);
            this.actionableObject = actionableObject;
            this.length++;
        }
    }

    public Actionable get(int pos) { return subList(pos).actionableObject; }

    public ActionableList subList(int pos)
    {
        if(pos < 0 || pos >= this.length)
        {
            System.err.println("ERROR INVALID POSITION");
            return null;
        }

        int dist = this.length - pos;

        ActionableList current = this;

        while(current.length > dist)
            current = current.previous;

        return current;
    }

    public boolean contains(Actionable actionableObject)
    {
        if(this.actionableObject == null)
            return false;

        ActionableList current = this;

        while(current != null)
        {
            if(current.actionableObject.equals(actionableObject))
                return true;

            current = current.previous;
        }

        return false;
    }

    public int length() { return this.length; }

    public Actionable[] toArray()
    {
        Actionable[] actionableArray = new Actionable[this.length];

        ActionableList actionableList = this;

        for(int actionableObject = this.length-1; actionableObject >= 0; actionableObject--)
        {
            actionableArray[actionableObject] = actionableList.actionableObject;
            actionableList = actionableList.previous;
        }

        return actionableArray;
    }

    public void print() { System.out.print(toString()); }

    public String toString()
    {
        if(this.length > 500)
            return iterativeToString();
        else
            return recursiveToString();
    }

    private String iterativeToString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        Actionable[] actionableArray = toArray();

        for(int actionableObject = 0; actionableObject < this.length; actionableObject++)
            stringBuilder.append(actionableArray[actionableObject] + "\n");

        return stringBuilder.toString();
    }

    private String recursiveToString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        recursiveToStringHelper(stringBuilder, this);

        return stringBuilder.toString();
    }

    private void recursiveToStringHelper(StringBuilder stringBuilder, ActionableList current)
    {
        if(current == null)
            return;

        recursiveToStringHelper(stringBuilder, current.previous);

        stringBuilder.append(current.actionableObject + "\n");
    }
}