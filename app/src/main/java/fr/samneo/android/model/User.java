package fr.samneo.android.model;

import android.os.Bundle;

import org.parceler.Parcel;
import org.parceler.Parcels;

import icepick.Bundler;

@Parcel
public class User implements Bundler<User> {
    private String m_firstName;
    private int m_score = 0;

    public String getFirstName() {
        return m_firstName;
    }

    public void setFirstName(String firstName) {
        m_firstName = firstName;
    }

    public int getScore() {
        return m_score;
    }

    public void setScore(int score) {
        m_score = score;
    }

    public User() {
    }

    @Override
    public void put(String s, User user, Bundle bundle) {
        bundle.putParcelable(s, Parcels.wrap(user));
    }

    @Override
    public User get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
