package implementation;

import annotations.IteratorPattern;

import java.util.ArrayList;
import java.util.List;

/***
 * Concrete Aggregate with annoations implemented
 * Has a private inner class which implements iterator
 */
@IteratorPattern.ConcreteAggregate
public class LinkedIn implements SocialNetwork {
    private List<Profile> contacts;

    public LinkedIn(List<Profile> cache) {
        if (cache != null) {
            this.contacts = cache;
        } else {
            this.contacts = new ArrayList<>();
        }
    }

    public Profile requestContactInfoFromLinkedInAPI(String profileEmail) {
        // Here would be a POST request to one of the LinkedIn API endpoints.
        // Instead, we emulates long network connection, which you would expect
        // in the real life...
        simulateNetworkLatency();
        System.out.println("LinkedIn: Loading profile '" + profileEmail + "' over the network...");

        // ...and return test data.
        return findContact(profileEmail);
    }

    public List<String> requestRelatedContactsFromLinkedInAPI(String profileEmail, String contactType) {
        // Here would be a POST request to one of the LinkedIn API endpoints.
        // Instead, we emulates long network connection, which you would expect
        // in the real life.
        simulateNetworkLatency();
        System.out.println("LinkedIn: Loading '" + contactType + "' list of '" + profileEmail + "' over the network...");

        // ...and return test data.
        Profile profile = findContact(profileEmail);
        if (profile != null) {
            return profile.getContacts(contactType);
        }
        return null;
    }

    private Profile findContact(String profileEmail) {
        for (Profile profile : contacts) {
            if (profile.getEmail().equals(profileEmail)) {
                return profile;
            }
        }
        return null;
    }

    private void simulateNetworkLatency() {
        System.out.println("....");
    }

    // Overirden factoryMethods
    @Override
    @IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = LinkedInIterator.class)
    public LinkedInIterator createFriendsIterator(String profileEmail) {
        return new LinkedInIterator(this, "friends", profileEmail);
    }

    // Overirden factoryMethods
    @Override
    @IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = LinkedInIterator.class)
    public LinkedInIterator createCoworkersIterator(String profileEmail) {
        return new LinkedInIterator(this, "coworkers", profileEmail);
    }

    //private concreteITerator
    @IteratorPattern.ConcreteIterator
    private class LinkedInIterator implements ProfileIterator {
        private LinkedIn linkedIn;
        private String type;
        private String email;
        private int currentPosition = 0;
        private List<String> emails = new ArrayList<>();
        private List<Profile> contacts = new ArrayList<>();

        public LinkedInIterator(LinkedIn linkedIn, String type, String email) {
            this.linkedIn = linkedIn;
            this.type = type;
            this.email = email;
        }

        private void lazyLoad() {
            if (emails.size() == 0) {
                List<String> profiles = linkedIn.requestRelatedContactsFromLinkedInAPI(this.email, this.type);
                for (String profile : profiles) {
                    this.emails.add(profile);
                    this.contacts.add(null);
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
            Profile friendContact = contacts.get(currentPosition);
            if (friendContact == null) {
                friendContact = linkedIn.requestContactInfoFromLinkedInAPI(friendEmail);
                contacts.set(currentPosition, friendContact);
            }
            currentPosition++;
            return friendContact;
        }

        @Override
        public void reset() {
            currentPosition = 0;
        }
    }
}
