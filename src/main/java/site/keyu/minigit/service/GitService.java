package site.keyu.minigit.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitService {
    private Logger logger = LoggerFactory.getLogger(GitService.class);

    //git log
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

    //git log SHA1 SHA2
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

    //git log SHA1
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

    //git branch
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

    //git diff SHA1 SHA2
    public List<DiffEntry> diff(Repository repo, CanonicalTreeParser oldTree,CanonicalTreeParser newTree){
        Git git = new Git(repo);
        try{
            List<DiffEntry> diffs =  git.diff().setNewTree(newTree).setOldTree(oldTree).call();
            return diffs;
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    //git notes show
    public Note notesShow(Repository repo,RevCommit revCommit){
        Git git = new Git(repo);
        try {
            Note note = git.notesShow().setObjectId(revCommit).call();
            return note;
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



}
