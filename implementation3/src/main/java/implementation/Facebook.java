package implementation;

import annotations.IteratorPattern;

import java.util.ArrayList;
import java.util.List;

/***
 * Concrete Aggregate with annoations implemented
 * Has a private inner class which implements iterator
 */
@IteratorPattern.ConcreteAggregate
public class Facebook implements SocialNetwork {
    private List<Profile> profiles;

    public Facebook(List<Profile> cache) {
        if (cache != null) {
            this.profiles = cache;
        } else {
            this.profiles = new ArrayList<>();
        }
    }

    public Profile requestProfileFromFacebook(String profileEmail) {
        // Here would be a POST request to one of the Facebook API endpoints.
        // Instead, we emulates long network connection, which you would expect
        // in the real life...
        simulateNetworkLatency();
        System.out.println("Facebook: Loading profile '" + profileEmail + "' over the network...");

        // ...and return test data.
        return findProfile(profileEmail);
    }

    public List<String> requestProfileFriendsFromFacebook(String profileEmail, String contactType) {
        // Here would be a POST request to one of the Facebook API endpoints.
        // Instead, we emulates long network connection, which you would expect
        // in the real life...
        simulateNetworkLatency();
        System.out.println("Facebook: Loading '" + contactType + "' list of '" + profileEmail + "' over the network...");

        // ...and return test data.
        Profile profile = findProfile(profileEmail);
        if (profile != null) {
            return profile.getContacts(contactType);
        }
        return null;
    }

    private Profile findProfile(String profileEmail) {
        for (Profile profile : profiles) {
            if (profile.getEmail().equals(profileEmail)) {
                return profile;
            }
        }
        return null;
    }

    private void simulateNetworkLatency() {
        System.out.println("...");
    }

    // Overirden factoryMethods
    @Override
    @IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = ProfileIterator.class)
    public ProfileIterator createFriendsIterator(String profileEmail) {
        return new FacebookIterator(this, "friends", profileEmail);
    }

    // Overirden factoryMethods
    @Override
    @IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = ProfileIterator.class)
    public ProfileIterator createCoworkersIterator(String profileEmail) {
        return new FacebookIterator(this, "coworkers", profileEmail);
    }
    //private concreteITerator
    @IteratorPattern.ConcreteIterator
    private class FacebookIterator implements ProfileIterator {
        private Facebook facebook;
        private String type;
        private String email;
        private int currentPosition = 0;
        private List<String> emails = new ArrayList<>();
        private List<Profile> profiles = new ArrayList<>();

        public FacebookIterator(Facebook facebook, String type, String email) {
            this.facebook = facebook;
            this.type = type;
            this.email = email;
        }

        private void lazyLoad() {
            if (emails.size() == 0) {
                List<String> profiles = facebook.requestProfileFriendsFromFacebook(this.email, this.type);
                for (String profile : profiles) {
                    this.emails.add(profile);
                    this.profiles.add(null);
                }
            }
        }

        @Override
        public boolean hasNext() {
            lazyLoad();
            return currentPosition < emails.size();
        }

        @Override
        public Profile getNext() {
            if (!hasNext()) {
                return null;
            }

            String friendEmail = emails.get(currentPosition);
            Profile friendProfile = profiles.get(currentPosition);
            if (friendProfile == null) {
                friendProfile = facebook.requestProfileFromFacebook(friendEmail);
                profiles.set(currentPosition, friendProfile);
            }
            currentPosition++;
            return friendProfile;
        }

        @Override
        public void reset() {
            currentPosition = 0;
        }
    }

}
