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
    public List<DiffEntry> getDiffByCommitId(Repository repo,String commitId){
        ObjectReader objectReader = repo.newObjectReader();
        try {
            ObjectId id  = repo.resolve(commitId);
            RevWalk revWalk = new RevWalk(repo);
            RevCommit revCommit = revWalk.parseCommit(id);
            ObjectId treeId = revCommit.getTree().getId();
            RevCommit revCommit1 = revWalk.parseCommit(revCommit.getParent(0).getId());
            //ObjectId parentTreeId = revCommit.getParent(0).getTree().getId();

            ObjectId parentTreeId = revCommit1.getTree().getId();

            CanonicalTreeParser parentTreeParser = new CanonicalTreeParser();
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(objectReader,treeId);
            parentTreeParser.reset(objectReader,parentTreeId);

            return this.gitService.diff(repo,parentTreeParser,treeParser);
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return null;

    }


}
