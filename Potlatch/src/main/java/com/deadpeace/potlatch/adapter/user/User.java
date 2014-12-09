/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package com.deadpeace.potlatch.adapter.user;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable,Serializable
{
    private long id;
    private List<User> friends;
    private String username,preference;

    private User(Parcel parcel)
    {
        id=parcel.readLong();
        username=parcel.readString();
        friends=new ArrayList<User>();
        parcel.readList(friends,User.class.getClassLoader());
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username=username;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id=id;
    }

    public String getPreference()
    {
        return preference;
    }

    public void setPreference(String preference)
    {
        this.preference=preference;
    }

    public List<User> getFriends()
    {
        return friends;
    }

    public void setFriends(List<User> friends)
    {
        this.friends=friends;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof User)
        {
            User other=(User) obj;
            return Objects.equal(id, other.id);
        }
        else
            return false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeList(friends);
    }

    public static final Creator<User> CREATOR=new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel source)
        {
            return new User(source);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };
}
