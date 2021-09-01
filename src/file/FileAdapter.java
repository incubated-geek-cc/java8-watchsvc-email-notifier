package file;

public abstract class FileAdapter implements FileListener {
    @Override
    public void onCreated(FileEvent event) {
        // no implementation provided
        
    }

    @Override
    public void onModified(FileEvent event) {
        // no implementation provided

    }

    @Override
    public void onDeleted(FileEvent event) {

    }
}
