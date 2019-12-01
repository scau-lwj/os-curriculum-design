package file.operation;

import disk.bean.DiskBlock;
import disk.service.DiskService;
import file.bean.Catalog;
import file.bean.CatalogEntry;

import java.util.ArrayList;

/**
 * @author Rorke
 * @date 2019/11/11 12:38
 */
public class MoveOperation extends AbstractOperation {
    private String[] oldPath;
    private String[] newPath;
    private ArrayList<Catalog> oldFullCatalog;
    private ArrayList<Catalog> newFullCatalog;
    private DiskBlock[] FATBlocks;

    /**
     * 构造器
     *
     * @param oldPath   旧的路径
     * @param newPath   新的路径
     * @param FATBlocks fat表
     */
    public MoveOperation(String oldPath, String newPath, DiskBlock[] FATBlocks) {
        this.oldPath = oldPath.split("/");
        this.newPath = newPath.split("/");
        this.FATBlocks = FATBlocks;
        oldFullCatalog = getCatalogByPath(this.oldPath,FATBlocks);
        newFullCatalog = getCatalogByPath(this.newPath,FATBlocks);
    }

    /**
     * 移动文件
     *
     * @param fileName     文件名字
     * @param expandedName 扩展名
     * @return 移动是否成功
     */
    public boolean moveFile(String fileName, String expandedName) {
        DeleteOperation deleteOperation = new DeleteOperation();
        CatalogEntry oldEntry = fileUtils.getTargetEntryByCatalog(fileName,expandedName,oldFullCatalog);
        deleteOperation.modifyCatalog(oldEntry,oldFullCatalog);
        CatalogEntry newEntry = fileUtils.getTargetEntryByCatalog(fileName,expandedName,newFullCatalog);
        boolean statement = true;
        if(newEntry!=null&&!newEntry.isEmpty()){
            statement = false;
        }else {
            if (newEntry == null) {
                int nextEmptyIndex = fileUtils.getEmptyBlockIndex(FATBlocks);
                fileUtils.modifyFAT(newFullCatalog.get(newFullCatalog.size() - 1).getIndex(), nextEmptyIndex, FATBlocks);
                DiskService diskService = DiskService.getInstance();
                Catalog tmpNewCatalog = new Catalog(diskService.getDiskBlock(nextEmptyIndex));
                tmpNewCatalog.getEntries()[0].setContext(oldEntry.getContext());
            } else {
                newEntry.setContext(oldEntry.getContext());
            }
            oldEntry.setEmpty();
        }
        return  statement;
    }

    /**
     * 通过路径来获取目录
     *
     * @param path      路径
     * @param FATBlocks fat表
     * @return 完整目录
     */
    private ArrayList<Catalog> getCatalogByPath(String[] path, DiskBlock[] FATBlocks){
        DiskService diskService = DiskService.getInstance();
        Catalog rootCatalog = new Catalog(diskService.getDiskBlock(2));
        ArrayList<Catalog> tmpFullCatalog;
        CatalogEntry entry;
        for(String str:path){
            tmpFullCatalog = fileUtils.getFullCatalog(rootCatalog,FATBlocks);
            entry = fileUtils.getTargetEntryByCatalog(formatName(str),"D",tmpFullCatalog);
            rootCatalog = new Catalog(diskService.getDiskBlock(entry.getStartedBlockIndex()));
        }
        return fileUtils.getFullCatalog(rootCatalog,FATBlocks);
    }

    private String formatName(String name){
        StringBuilder nameBuilder = new StringBuilder(name);
        while (nameBuilder.length()<3){
            nameBuilder.append((char) 0);
        }
        name = nameBuilder.toString();
        return name;
    }
}
