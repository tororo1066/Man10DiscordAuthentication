package tororo1066.man10discordauthentication;

import tororo1066.tororopluginapi.AbstractDependencyLoader;
import tororo1066.tororopluginapi.Library;
import tororo1066.tororopluginapi.LibraryType;
import tororo1066.tororopluginapi.Repository;

public class DependencyLoader extends AbstractDependencyLoader {
    public DependencyLoader() {}

    @Override
    public Library[] getDependencies() {
        return new Library[]{
                LibraryType.KOTLIN.createLibrary(),
//                LibraryType.MONGODB.createLibrary(),
                new Library("net.dv8tion:JDA", "5.2.1", Repository.MAVEN_CENTRAL.url, "compile")
        };
    }
}
