package uk.co.williammayor.simpact.accuracy;

public class Torrent {
    
    private final int id;
    
    public Torrent(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Torrent other = (Torrent) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
