package site.keyu.minigit.service;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommitService {

    @Autowired
    private GitService gitService;

    private Logger logger = LoggerFactory.getLogger(CommitService.class);

    public RevCommit getRevCommitByCommitId(Repository repo,String commitId){
        try {
            ObjectId id = repo.resolve(commitId);
            RevWalk revWalk = new RevWalk(repo);
            RevCommit revCommit = revWalk.parseCommit(id);
            return revCommit;
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<DiffEntry> getDiffByCommitId(Repository repo,String commitId){
        ObjectReader objectReader = repo.newObjectReader();
        try {
            ObjectId id  = repo.resolve(commitId);
            RevWalk revWalk = new RevWalk(repo);
            RevCommit revCommit = revWalk.parseCommit(id);

            ObjectId treeId = revCommit.getTree().getId();
            RevCommit[] parent = revCommit.getParents();
            //暂时无法处理没有父commit的情况（即使repo的第一个commit）
            if (parent.length > 0){

                return this.getDiff(repo,revCommit.getParent(0).getName(),commitId);
            }

        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<DiffEntry> getDiff(Repository repo,String oldCommitId,String newCommitId){
        try {
            ObjectReader objectReader = repo.newObjectReader();
            ObjectId oldId = repo.resolve(oldCommitId);
            ObjectId newId = repo.resolve(newCommitId);
            RevWalk revWalk = new RevWalk(repo);
            RevCommit oldRevCommit = revWalk.parseCommit(oldId);
            RevCommit newRevCommit = revWalk.parseCommit(newId);
            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(objectReader,oldRevCommit.getTree().getId());
            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(objectReader,newRevCommit.getTree().getId());


            return this.gitService.diff(repo,oldTree,newTree);
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}
