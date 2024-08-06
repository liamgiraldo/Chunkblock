package me.liamgiraldo.chunkblock.util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BoundingBox {
    private double minx,  minz;
    private double maxx, maxz;


    public BoundingBox(double minx, double minz, double maxx, double maxz){
        this.minx = minx;
        this.maxx = maxx;
        this.minz = minz;
        this.maxz = maxz;
    }

    public BoundingBox(Block block){
        this(block.getX(), block.getZ(),block.getX() + 1, block.getZ() + 1);
    }

    public BoundingBox(Vector corner1, Vector corner2){
        this(Math.min(corner1.getX(),corner2.getX()),  Math.min(corner1.getZ(),corner2.getZ()),
                Math.max(corner1.getX(),corner2.getX()),  Math.max(corner1.getZ(),corner2.getZ()));
    }

    public BoundingBox(Location center, double radius){
        this(center.getX() - radius, center.getZ() - radius, center.getX() + radius, center.getZ() + radius);
    }

    public boolean overlaps(double minx, double minz, double maxx,  double maxz){
        return this.minx < maxx && this.maxx > minx  && this.minz < maxz && this.maxz > minz;
    }

    public boolean overlaps(Block block){
        int mx = block.getX() + 1;
        int mz = block.getZ() + 1;
        return this.overlaps(block.getX(),block.getZ(), mx, mz );
    }


    /**
     * Check whether a point is within the bounding box
     * @param x x coord
     * @param z z coord
     * @return true if the given (x,z) point is contained within the bounding box.
     * Note that bounding boxes are rightwards exclusionary, but leftwards inclusionary. (A point on the rightmost edge is not considered to be in the box)
     */
    public boolean contains(double x, double z){
        return x >= minx && x < maxx && z >= minz && z < maxz;
    }



    public double minX(){ return minx; }
    public double minZ(){ return minz; }

    public double maxX(){ return maxx; }
    public double maxZ(){ return maxz; }

}
