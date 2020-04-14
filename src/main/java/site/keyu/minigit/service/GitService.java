package site.keyu.minigit.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitService {
    private Logger logger = LoggerFactory.getLogger(GitService.class);

    public Iterable<RevCommit> log(Repository repo){
        Git git = new Git(repo);
        try {
            Iterable<RevCommit> it = git.log().call();
            return it;
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

    public Iterable<RevCommit> log(Repository repo, ObjectId since,ObjectId to){
        Git git = new Git(repo);
        try {
            Iterable<RevCommit> it = git.log().addRange(since,to).call();
            return it;
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

    public Iterable<RevCommit> log(Repository repo, ObjectId start){
        Git git = new Git(repo);
        try {
            Iterable<RevCommit> it = git.log().add(start).call();
            return it;
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Ref> branch(Repository repo){
        Git git = new Git(repo);
        try {
            List<Ref> refs = git.branchList().call();
            return refs;
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

    public Object diff(Repository repo){
        Git git = new Git(repo);
        try{
            List<DiffEntry> diffs =  git.diff().call();
            return diffs;
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }


}
