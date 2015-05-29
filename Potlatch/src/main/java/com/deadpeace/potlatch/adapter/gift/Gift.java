package com.deadpeace.potlatch.adapter.gift;

import android.os.Parcel;
import android.os.Parcelable;
import com.deadpeace.potlatch.adapter.user.User;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 13.10.2014
 * Time: 11:47
 * To change this template use File | Settings | File Templates.
 */

public class Gift implements Comparable<Gift>,Parcelable
{
    private long id;
    private String title;
    private String description;
    private List<User> liked;
    private List<User> obscene;
    private List<User> recipients;
    private User creator;
    private Date date;

    public Gift()
    {

    }

    private Gift(Parcel parcel)
    {
        id=parcel.readLong();
        date=new Date(parcel.readLong());
        obscene=new ArrayList<User>();
        parcel.readList(obscene,User.class.getClassLoader());
        liked=new ArrayList<User>();
        parcel.readList(liked,User.class.getClassLoader());
        recipients=new ArrayList<User>();
        parcel.readList(recipients,User.class.getClassLoader());
        title=parcel.readString();
        description=parcel.readString();
        creator=(User)parcel.readSerializable();
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id=id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title=title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description=description;
    }

    public User getCreator()
    {
        return creator;
    }

    public void setCreator(User creator)
    {
        this.creator=creator;
    }

    public List<User> getLiked()
    {
        return liked;
    }

    public void setLiked(List<User> liked)
    {
        this.liked=liked;
    }

    public List<User> getObscene()
    {
        return obscene;
    }

    public void setObscene(List<User> obscene)
    {
        this.obscene=obscene;
    }

    public List<User> getRecipients()
    {
        return recipients;
    }

    public void setRecipients(List<User> recipients)
    {
        this.recipients=recipients;
    }

    public long getCountLike()
    {
        return liked!=null?liked.size():0;
    }

    public long getCountObscene()
    {
        return obscene!=null?obscene.size():0;
    }

    public boolean isLikes(User user)
    {
        return liked!=null&&liked.contains(user);
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date=date;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Gift)
        {
            Gift other=(Gift) obj;
            return Objects.equal(id, other.id);
        }
        else
            return false;
    }

    @Override
    public int compareTo(Gift another)
    {

        if(this.getCountLike()==another.getCountLike())
            return 0;
        else
            if(this.getCountLike()>another.getCountLike())
                return -1;
            else
                return 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags)
    {
        dest.writeLong(id);
        dest.writeLong(date.getTime());
        dest.writeList(obscene);
        dest.writeList(liked);
        dest.writeList(recipients);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeSerializable(creator);
    }

    public static final Creator<Gift> CREATOR=new Creator<Gift>()
    {
        @Override
        public Gift createFromParcel(Parcel source)
        {
            return new Gift(source);
        }

        @Override
        public Gift[] newArray(int size)
        {
            return new Gift[size];
        }
    };
}
