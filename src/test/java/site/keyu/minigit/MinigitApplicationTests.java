package site.keyu.minigit;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.keyu.minigit.config.GitEnv;
import site.keyu.minigit.service.CommitService;
import site.keyu.minigit.service.GitService;
import site.keyu.minigit.service.RepositoryService;
import site.keyu.minigit.util.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@SpringBootTest
class MinigitApplicationTests {

    @Autowired
    CommitService commitService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    GitService gitService;

    @Autowired
    ZipUtil zipUtil;

    @Autowired
    GitEnv env;

    @Test
    void test() {
        //Repository repo = this.repositoryService.buildRepo("User1","MyGit");
        String source = this.env.basepath + "\\User1\\MyGit";
        String target = this.env.tempPath + "\\target.zip";
        try {
            OutputStream outputStream = new FileOutputStream(new File(target));
            this.zipUtil.toZip(source,outputStream);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    RevCommit getNextCommit(Repository repo,RevCommit revCommit){
        try {
            RevWalk revWalk = new RevWalk(repo);
            revWalk.markStart(revCommit);
            int i = 0;
            RevCommit nextCommit;
            for (RevCommit commit:revWalk){
                System.out.println(i);
                if (i==1){
                    nextCommit = commit;
                    return nextCommit;
                }
                i++;
            }
        }catch (Exception e){

        }
        return null;

    }

}
