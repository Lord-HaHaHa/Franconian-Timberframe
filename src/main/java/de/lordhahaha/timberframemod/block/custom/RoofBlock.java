package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.block.ModBlocks;
import de.lordhahaha.timberframemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.IntStream;

public class RoofBlock extends Block{
    static final int STATE_ROOF = 0;
    static final int STATE_CORNER_OUTER = 1;
    static final int STATE_CORNER_INNER = 2;
    static final int STATE_GABLE = 3;
    static final int STATE_TOP = 4;
    static final int STATE_TOP_EDGE = 5;
    static final int STATE_TOP_CROSS = 6;
    static final int STATE_TOP_T = 7;
    static final int STATE_TOP_L = 8;
    static final int STATE_TOP_CENTER = 9;
    static final int NEIGHBOUR_LEFT = 1;
    static final int NEIGHBOUR_RIGHT = 2;
    static final int NEIGHBOUR_INFORNT = 3;
    static final int NEIGHBOUR_BEHIND = 4;
    static final Set<Integer> NON_CORNER_ROOFS = Set.of(STATE_ROOF, STATE_GABLE);
    static final Set<Integer> CORNER_ROOFS = Set.of(STATE_CORNER_OUTER, STATE_CORNER_INNER);
    static final Set<Integer> ROOFS_TOPS = Set.of(STATE_TOP, STATE_TOP_EDGE, STATE_TOP_CROSS, STATE_TOP_T, STATE_TOP_L, STATE_TOP_CENTER);

    private static final VoxelShape TOP_SHAPE = Block.box(0,0,0,16,5,16);
    private static final VoxelShape BASE_SHAPE = Block.box(0,0,0,16,8,16);
    protected static final VoxelShape OCTET_NNN = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
    protected static final VoxelShape OCTET_NNP = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape OCTET_NPN = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape OCTET_NPP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape OCTET_PNN = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape OCTET_PNP = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape OCTET_PPN = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape OCTET_PPP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    //protected static final VoxelShape[] TOP_SHAPES = makeShapes(BASE_SHAPE, OCTET_NNN, OCTET_PNN, OCTET_NNP, OCTET_PNP);
    protected static final VoxelShape[] BOTTOM_SHAPES = makeShapes(BASE_SHAPE, OCTET_NPN, OCTET_PPN, OCTET_NPP, OCTET_PPP);
    private static final int[] SHAPE_BY_STATE = new int[]{
            0b1100, 0b0101, 0b0011, 0b1010,  //ROOF
            0b0100, 0b0001, 0b0010, 0b1000,  //CORNER_OUTER
            0b1101, 0b0111, 0b1011, 0b1110,  //CORNER_INNER
            0b1100, 0b0101, 0b0011, 0b1010   //GABLE
            };

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final DirectionProperty FACING_ORG = DirectionProperty.create("facing_org", Direction.Plane.HORIZONTAL);
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 9);
    public static final BooleanProperty MANUAL = BooleanProperty.create("manual");
    public Block ROOF_BLOCK;

    public RoofBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MANUAL,false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING_ORG, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, STATE_GABLE));
    }
    private static VoxelShape[] makeShapes(VoxelShape shape1, VoxelShape shape2, VoxelShape shape3, VoxelShape shape4, VoxelShape shape5) {
        return IntStream.range(0, 16).mapToObj((state) -> {
            return makeStairShape(state, shape1, shape2, shape3, shape4, shape5);
        }).toArray((p_56949_) -> {
            return new VoxelShape[p_56949_];
        });
    }

    private static VoxelShape makeStairShape(int state, VoxelShape shape1, VoxelShape shape2, VoxelShape shape3, VoxelShape shape4, VoxelShape shape5) {
        VoxelShape voxelshape = shape1;
        if ((state & 1) != 0) {
            voxelshape = Shapes.or(shape1, shape2);
        }

        if ((state & 2) != 0) {
            voxelshape = Shapes.or(voxelshape, shape3);
        }

        if ((state & 4) != 0) {
            voxelshape = Shapes.or(voxelshape, shape4);
        }

        if ((state & 8) != 0) {
            voxelshape = Shapes.or(voxelshape, shape5);
        }

        return voxelshape;
    }

    private int getShapeIndex(BlockState state) {
        return state.getValue(STATE) * 4 + state.getValue(FACING).get2DDataValue();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if ( blockState.getValue(STATE) < STATE_TOP) {
            return (BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(blockState)]];
        }
        else{
            return TOP_SHAPE;
        }
        //return super.getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack held = player.getItemInHand(interactionHand);
        ROOF_BLOCK = ModBlocks.BLOCK_ROOF_MAIN.get();
        if (!level.isClientSide()) {
            //run on server only
            Direction facingSelf = blockState.getValue(FACING);

            if (held.getItem() == ModItems.CARPENTERS_HAMMER.get()) {
                //first rotate. if block is NORTH, we change state
                if (facingSelf.equals(Direction.NORTH)){
                    int state = blockState.getValue(STATE);
                    blockState = blockState.setValue(MANUAL, true);
                    if (state < STATE_TOP) {
                        state++;
                        if (state >= STATE_TOP) {
                            state = STATE_ROOF;
                        }
                    }
                    if (state >= STATE_TOP) {
                        state++;
                        if (state >= STATE_TOP_CENTER) {
                            state = STATE_TOP;
                        }
                    }
                    blockState = blockState.setValue(STATE, state);
                    blockState = blockState.setValue(FACING, Direction.NORTH.getClockWise());
                } else {
                    blockState = blockState.setValue(FACING, facingSelf.getClockWise());
                }

                //System.out.println(state);
                level.setBlockAndUpdate(blockPos, blockState);
            }
            // Only for debugging
            // TODO Remove before release
            else if(held.getItem() == Items.STICK){
                System.out.println("Block In line: " + roofInLine(blockState, level, blockPos));
                System.out.println("Block In Corner: " + roofInCorner(blockState, level, blockPos));
                System.out.println("Neighbor for TOP: " + getNeighborIndex(level, blockState, blockPos, STATE_TOP));
            }
            return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        ROOF_BLOCK = ModBlocks.BLOCK_ROOF_MAIN.get();
        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(FACING_ORG, context.getHorizontalDirection())
                .setValue(STATE, this.defaultBlockState().getValue(STATE))
                .setValue(MANUAL, false);

        // Get blockState depending on its Neighbors
        blockState = checkForGable(blockState, level, blockPos);

        blockState = checkForTop(blockState, level, blockPos);

        System.out.println(" -- Blockplacement before Coner-- ");
        System.out.println(blockState.getValue(STATE));
        System.out.println(blockState.getValue(FACING));
        System.out.println(blockState.getValue(FACING_ORG));

        // Roof cant be Coner if ROOFTOP
        if(ROOFS_TOPS.contains(blockState.getValue(STATE)))
            return blockState;

        blockState = checkForCornerPlacement(blockState, level, blockPos);

        return blockState;
    }



    private void updateBlockIfChaged(BlockState newBlockState, BlockState oldBlockState, Level level, BlockPos blockPos) {
        if(!newBlockState.equals(oldBlockState)){
            level.setBlockAndUpdate(blockPos, newBlockState);
        }
    }
    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block replacedBlock, BlockPos blockPosNeighbor, boolean bool) {
        super.neighborChanged(blockState, level, blockPos, replacedBlock, blockPosNeighbor, bool);
        ROOF_BLOCK = ModBlocks.BLOCK_ROOF_MAIN.get();

        BlockState blockStateBackup = blockState;
        Direction facingSelf = blockState.getValue(FACING);

        /*
        System.out.println(" --- NeighborChanged --- ");
        System.out.println("BlockState Self: " + blockState);
        System.out.println("BlockPos Self: " + blockPos);
        System.out.println("Replaced Block: " + replacedBlock);
        System.out.println("Block Pos Changed: " + blockPosNeighbor);
        System.out.println("unknown Bool: " + bool);
        */

        // No Update if Block was changed with a tool
        if(blockState.getValue(MANUAL))
            return;

        // Check if Block is now inline and not a valid Corner
        if(roofInLine(blockState, level, blockPos) ) {
            blockState = blockState.setValue(STATE, STATE_GABLE);
            blockState = blockState.setValue(FACING, blockState.getValue(FACING_ORG));
            updateBlockIfChaged(blockState, blockStateBackup, level, blockPos);
            return;
        }

        // Check if Block is another rooftop
        // Roof cant be Coner if ROOFTOP
        BlockState blockStateTop = checkForTop(blockState, level, blockPos);
        if(ROOFS_TOPS.contains(blockStateTop.getValue(STATE))){
            updateBlockIfChaged(blockStateTop, blockStateBackup, level, blockPos);
            return;
        }

        // Check if Block is now a Corner
        blockState = checkForCornerPlacement(blockState, level, blockPos);
        updateBlockIfChaged(blockState, blockStateBackup, level, blockPos);
        return;
    }

    public BlockState checkForCornerPlacement(BlockState blockState, Level level, BlockPos blockPos) {
        ROOF_BLOCK = ModBlocks.BLOCK_ROOF_MAIN.get();
        BlockState blockStateOld = blockState;
        Direction facingSelf = blockState.getValue(FACING);

        // New Block cant be a corner if he is in line with its left and right neighbour
        if(roofInLine(facingSelf, level, blockPos))
            return blockStateOld;

        blockState = getCornerShape(facingSelf, blockState, level, blockPos);

        return blockState;
    }

    private boolean roofInLine(BlockState blockState, Level level, BlockPos blockPos) {
        return roofInLine(blockState.getValue(FACING_ORG), level, blockPos);
    }

    /**
     * @param facingSelf facing of the Roof Block
     * @param level
     * @param blockPos Position of the Block
     * @return TRUE when the Roofblock is in a line with its left and right neighbour
     */
    private boolean roofInLine(Direction facingSelf, Level level, BlockPos blockPos){
        boolean leftNeighbourCorrect = roofHasCorrectNeighbour(facingSelf, NEIGHBOUR_LEFT, level , blockPos);
        boolean rightNeighbourCorrect = roofHasCorrectNeighbour(facingSelf, NEIGHBOUR_RIGHT, level , blockPos);

        return (leftNeighbourCorrect && rightNeighbourCorrect);
    }

    private BlockState getCornerShape(Direction facingSelf, BlockState blockState, Level level, BlockPos blockPos) {
        // New Block is an Outer-Corner when the block behind has a 90° or -90° rotated facing
        BlockPos blockPosBehind = blockPos.relative(facingSelf);
        BlockState blockStateBehind = level.getBlockState(blockPosBehind);

        if(blockStateBehind.getBlock().equals(ROOF_BLOCK)) {
            Direction facingBehind = blockStateBehind.getValue(FACING_ORG);
            
            if(facingSelf == facingBehind.getCounterClockWise()) {
                if(blockStateBehind.getValue(STATE) != STATE_CORNER_INNER)
                    blockState = blockState.setValue(STATE, STATE_CORNER_OUTER);
                return blockState;
            }

            if (facingSelf == facingBehind.getClockWise()) {
                blockState = blockState.setValue(STATE, STATE_CORNER_OUTER);
                blockState = blockState.setValue(FACING, facingSelf.getCounterClockWise());
                return blockState;
            }

            if(blockStateBehind.getValue(STATE) == STATE_CORNER_INNER && facingSelf == blockStateBehind.getValue(FACING)){
                blockState = blockState.setValue(STATE, STATE_CORNER_OUTER);
                return blockState;
            }
        }
        // New Block is an Inner-Corner when the block infront has a 90° or -90° rotated facing
        // and the block on its left is not a roof block
        BlockPos blockPosInfront = blockPos.relative(facingSelf.getOpposite());
        BlockState blockStateInfront = level.getBlockState(blockPosInfront);

        if (blockStateInfront.getBlock().equals(ROOF_BLOCK)) {
            Direction facingInfront = blockStateInfront.getValue(FACING_ORG);
            if(facingSelf == facingInfront.getCounterClockWise() || facingSelf == facingInfront.getClockWise()) {
                // guards for inner corner
                if(roofHasCorrectNeighbour(facingSelf, NEIGHBOUR_RIGHT, level, blockPos) &&
                        facingSelf == facingInfront.getCounterClockWise())
                    return blockState;
                if(roofHasCorrectNeighbour(facingSelf, NEIGHBOUR_LEFT, level, blockPos) &&
                        facingSelf == facingInfront.getClockWise())
                    return blockState;

                // Change facing if the coner is left side
                if (facingSelf == facingInfront.getClockWise()){
                    blockState = blockState.setValue(FACING, facingSelf.getCounterClockWise());
                }

                blockState = blockState.setValue(STATE, STATE_CORNER_INNER);

                return blockState;
            }
        }
        return blockState;
    }

    /**
     * @param facingSelf facing of the Roof Block
     * @param side 1=Left, 2=Right, 3=Infront, 4=Behind -Neighobur
     * @param blockPos Position of the Block
     * @return TRUE if the Roofblock is in a line with its neighbour
     */
    private boolean roofHasCorrectNeighbour(Direction facingSelf, int side, Level level, BlockPos blockPos){
        Direction neighbourDirection;
        if(side == NEIGHBOUR_LEFT) {
            neighbourDirection = facingSelf.getCounterClockWise();
        } else if (side == NEIGHBOUR_RIGHT) {
            neighbourDirection = facingSelf.getClockWise();
        } else if(side == NEIGHBOUR_INFORNT) {
            neighbourDirection = facingSelf.getOpposite();
        } else if(side == NEIGHBOUR_BEHIND) {
            neighbourDirection = facingSelf;
        } else
            return false;

        Direction facingNeighbour = getRelativeFacing(neighbourDirection, level, blockPos);
        int stateNeighbour = getRelativeState(neighbourDirection, level, blockPos);
        if(stateNeighbour == -1){
            return false;
        }

        Direction facing_horizontal = facingNeighbour.getClockWise();
        if(side == NEIGHBOUR_LEFT) {
            if(stateNeighbour == STATE_CORNER_INNER)
                return(facing_horizontal == facingSelf);
            else if(stateNeighbour == STATE_CORNER_OUTER)
                return(facingNeighbour == facingSelf);
            else if(NON_CORNER_ROOFS.contains(stateNeighbour))
                return facingNeighbour == facingSelf;
        } else if (side == NEIGHBOUR_RIGHT){
            if(stateNeighbour == STATE_CORNER_INNER)
                return (facingNeighbour == facingSelf);
            else if(stateNeighbour == STATE_CORNER_OUTER)
                return (facing_horizontal == facingSelf);
            else if(NON_CORNER_ROOFS.contains(stateNeighbour))
                return facingNeighbour == facingSelf;
        } else if(side == NEIGHBOUR_INFORNT) {
            if(stateNeighbour == STATE_CORNER_INNER)
                return (facingNeighbour.getCounterClockWise() == facingSelf);
            else if(stateNeighbour == STATE_CORNER_OUTER)
                return (facingNeighbour == facingSelf);
            if(NON_CORNER_ROOFS.contains(stateNeighbour))
                return(facingNeighbour.getCounterClockWise() == facingSelf);
        } else if(side == NEIGHBOUR_BEHIND){
            if(stateNeighbour == STATE_CORNER_INNER)
                return (facingNeighbour == facingSelf);
            else if(stateNeighbour == STATE_CORNER_OUTER)
                return (facingNeighbour.getCounterClockWise() == facingSelf);
            if(NON_CORNER_ROOFS.contains(stateNeighbour))
                return(facingNeighbour.getCounterClockWise() == facingSelf);
            return(facing_horizontal == facingSelf);
        }
        return false;
    }

    /**
     * @param blockStateSelf facing of the Corner to check
     * @param level
     * @param blockPos Position of the Block
     * @return TRUE when a Corner Has on both Sides a correct facing Roof block
     */
    private boolean roofInCorner(BlockState blockStateSelf, Level level, BlockPos blockPos) {
        Direction facingSelf = blockStateSelf.getValue(FACING);
        if(blockStateSelf.getValue(STATE) == STATE_CORNER_OUTER){
            boolean behindNeighbourCorrect = roofHasCorrectNeighbour(facingSelf, 4, level, blockPos);
            boolean rightNeighbourCorrect = roofHasCorrectNeighbour(facingSelf, 2, level , blockPos);
            return (behindNeighbourCorrect && rightNeighbourCorrect);
        } else if (blockStateSelf.getValue(STATE) == STATE_CORNER_INNER) {
            boolean infrontNeighbourCorrect = roofHasCorrectNeighbour(facingSelf, 3, level, blockPos);
            boolean leftNeighbourCorrect = roofHasCorrectNeighbour(facingSelf, 1, level , blockPos);
            return (infrontNeighbourCorrect && leftNeighbourCorrect);
        }
        return false;
    }

    public void neighborChangedOLD(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPosNeighbor, boolean bool) {
        super.neighborChanged(blockState, level, blockPos, block, blockPosNeighbor, bool);
        BlockState blockStateBackup = blockState;

        System.out.println("NeighborChanged");
        System.out.println("BlockState Self: " + blockState);
        System.out.println("BlockPos Self: " + blockPos);
        System.out.println("Block Self: " + block);
        System.out.println("Block Pos Changed: " + blockPosNeighbor);
        System.out.println("unknown Bool: " + bool);

        if(!(blockState.getValue(MANUAL))){
            if(block.equals(Blocks.AIR) || block.equals(ROOF_BLOCK)){
                if((blockState.getValue(STATE) < STATE_TOP && level.getBlockState(blockPosNeighbor.below()).getBlock().equals(Blocks.AIR)) ||
                        blockState.getValue(STATE) == STATE_CORNER_INNER || blockState.getValue(STATE) == STATE_CORNER_OUTER){

                    blockState = checkForGable(blockState, level, blockPos);
                }
                blockState = checkForTop(blockState, level, blockPos);
            }

            if(block.equals(Blocks.AIR)) { // Only check if a new block is placed
                Direction facingSelf = blockState.getValue(FACING);
                if(roofInLine(facingSelf, level, blockPos)){
                    Direction inLineFacing = getRelativeFacing(facingSelf.getClockWise(), level, blockPos);
                    blockState = blockState.setValue(STATE, STATE_ROOF);
                    if(inLineFacing != null)
                            blockState = blockState.setValue(FACING, getRelativeFacing(facingSelf.getClockWise(), level, blockPos));
                } else {
                    blockState = checkForCornerPlacement(blockState, level, blockPos);
                }
            }
        }

        // Update block in World
        if(!blockState.equals(blockStateBackup)){
            System.out.println("Update from UpdateNeighbour - BlockState(STATE): " + blockState.getValue(STATE));
            level.setBlockAndUpdate(blockPos, blockState);
        }
    }

    private Direction getRelativeFacing(Direction facingRelative, Level level, BlockPos blockPos){
        BlockPos blockPosRelative = blockPos.relative(facingRelative);
        BlockState blockStateRelative = level.getBlockState(blockPosRelative);
        if(blockStateRelative.getBlock().equals(ROOF_BLOCK))
            return blockStateRelative.getValue(FACING);
        return null;
    }

    private int getRelativeState(Direction facingRelative, Level level, BlockPos blockPos){
        BlockPos blockPosRelative = blockPos.relative(facingRelative);
        BlockState blockStateRelative = level.getBlockState(blockPosRelative);
        if(blockStateRelative.getBlock().equals(ROOF_BLOCK))
            return blockStateRelative.getValue(STATE);
        return -1;
    }

    /**
     * @param blockState State of the Roof Block
     * @param level
     * @param blockPos Position of the Block
     * @return adjusted BlockState for the RoofBlock
     */
    public BlockState checkForGable(BlockState blockState, Level level, BlockPos blockPos){
        // Checks if a given Block is a Gable

        BlockState blockStateClockwise = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getClockWise()));
        BlockState blockStateCounterClockwise = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getCounterClockWise()));
        BlockState blockStateBehind = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getOpposite()));
        BlockState blockStateInfront = level.getBlockState(blockPos.relative(blockState.getValue(FACING)));

        //do not change if Block it is not in line with other Roof-Blocks
        if(!blockStateClockwise.getBlock().equals(blockStateCounterClockwise.getBlock()) &&
                !blockStateInfront.getBlock().equals(blockStateBehind.getBlock()))
            return blockState;

        Block blockBelow = level.getBlockState(blockPos.below()).getBlock();

        //TODO mai-bee use ItemTag
        if(
            blockBelow.equals(Blocks.AIR) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_MAIN.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_INNER.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_OUTER.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_OVERHANG.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_OVERHANG_LEFTSTRUT.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_OVERHANG_RIGHTSTRUT.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_2WAY.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_3WAY.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_4WAY.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_EDGE.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_STAND_2WAY.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_STAND_3WAY.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_STAND_SOLO.get()) ||
            blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_STAND_EDGE.get())
        )
        {
            if(blockState.getValue(STATE) != STATE_ROOF) {
                    blockState = blockState.setValue(STATE, STATE_ROOF);
            }
        } else {
            if(blockState.getValue(STATE) != STATE_GABLE) {
                blockState = blockState.setValue(STATE, STATE_GABLE);
            }
        }

        // Reset earlier Rotations
        if(blockState.getValue(FACING) != blockState.getValue(FACING_ORG))
            blockState = blockState.setValue(FACING, blockState.getValue(FACING_ORG));

        return blockState;
    }

    public BlockState checkForTop(BlockState blockState, Level level, BlockPos blockPos){
        BlockPos blockPosBelow = blockPos.below();
        BlockPos blockPosBelowInfrontPos = blockPosBelow.relative(blockState.getValue(FACING));
        BlockPos blockPosBelowBehindPos = blockPosBelow.relative(blockState.getValue(FACING).getOpposite());
        BlockPos blockPosBelowClockwisePos = blockPosBelow.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosBelowCounterClockwisePos = blockPosBelow.relative(blockState.getValue(FACING).getCounterClockWise());

        Block blockBelowInfront = level.getBlockState(blockPosBelowInfrontPos).getBlock();
        Block blockBelowBehind = level.getBlockState(blockPosBelowBehindPos).getBlock();
        Block blockBelowClockwise = level.getBlockState(blockPosBelowClockwisePos).getBlock();
        Block blockBelowCounterClockwise = level.getBlockState(blockPosBelowCounterClockwisePos).getBlock();

        int neighbor = getNeighborIndex(level, blockState, blockPos, STATE_TOP);

        // Test if a Block is a Top-Block
        if((blockBelowInfront.equals(ROOF_BLOCK) && blockBelowBehind.equals(ROOF_BLOCK) ||
                blockBelowClockwise.equals(ROOF_BLOCK) && blockBelowCounterClockwise.equals(ROOF_BLOCK)))
        {
            // Test if a Top Block has 2 Neighbors on one Axis
            if(neighbor == 5 || neighbor == 10)
            {
                // Set Block to a Top Block
                if(blockState.getValue(STATE) != STATE_TOP) {
                    // Rotate block if neihbors are not in line with the Block
                    if(neighbor == 5) {
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                    }
                    blockState = blockState.setValue(STATE, STATE_TOP);
                }
            } else {
                if(neighbor == 0 || neighbor == 1 || neighbor == 2 || neighbor == 4 || neighbor == 8) // Check if it has none / only 1 Neighbor
                {
                    //check for center
                    if (neighbor==0 ){
                            if(blockState.getValue(STATE) != STATE_TOP_CENTER){
                                blockState = blockState.setValue(STATE, STATE_TOP_CENTER);
                            }
                    } else {
                        BlockState backupBlockState = blockState;
                        // Rotate Block 90deg if placed vertical on rooftop
                        if (blockBelowInfront.equals(ROOF_BLOCK) && blockBelowBehind.equals(ROOF_BLOCK)) {
                            blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                        }

                        //Update neighbor after Rotation for inline Placement to determan if edge needs to be fliped
                        neighbor = getNeighborIndex(level, blockState, blockPos, STATE_TOP);

                        // Rotate 180deg if Edge block is facing to a anthoer EdgeBlock
                        if (neighbor == 8)
                            blockState = blockState.setValue(FACING, blockState.getValue(FACING).getOpposite()); // Rotate so the Block is facing in the right Direction

                        if (!blockState.equals(backupBlockState) || blockState.getValue(STATE) != STATE_TOP_EDGE) {
                            blockState = blockState.setValue(STATE, STATE_TOP_EDGE);
                        }
                    }
                }
            }
        } else {
            // Check if Block is a Top-Center Block (all side are roofs)
            if(neighbor == 15){
                if(blockState.getValue(STATE) != STATE_TOP_CROSS){
                    blockState = blockState.setValue(STATE, STATE_TOP_CROSS);
                }
            } else {
                // Check if Block is a 3Way-Top Block
                if(neighbor == 7 || neighbor == 11 || neighbor == 13 || neighbor == 14){ // Check if it has 3 neighbors
                    // Set block to 3Way-Top Block
                    if(blockState.getValue(STATE) != STATE_TOP_T){

                        // Reset earlier Rotations
                        if(blockState.getValue(FACING) != blockState.getValue(FACING_ORG)) {
                            blockState = blockState.setValue(FACING, blockState.getValue(FACING_ORG));
                        }
                        neighbor = getNeighborIndex(level, blockState, blockPos, STATE_TOP);

                        // Rotate Block depending on its neighbors
                        System.out.println(neighbor);
                        switch (neighbor) {
                            case (7): {
                                blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());
                                break;
                            }
                            case (14): {
                                blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise().getCounterClockWise());
                                break;
                            }
                            case (13): {
                                blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                                break;
                            }
                        }
                        blockState = blockState.setValue(STATE, STATE_TOP_T);
                    }
                } else {
                    // Check if Block is a 2 Way-Top Block
                    if(neighbor == 3 || neighbor == 6 || neighbor == 12 || neighbor == 9){ // Check if Block has 2 neighbors
                        {
                            if(blockState.getValue(STATE) != STATE_TOP_L){
                                // Reset earlier Rotations
                                if(blockState.getValue(FACING) != blockState.getValue(FACING_ORG)) {
                                    blockState = blockState.setValue(FACING, blockState.getValue(FACING_ORG));
                                }
                                neighbor = getNeighborIndex(level, blockState, blockPos, STATE_TOP);

                                Direction facingSelf = blockState.getValue(FACING);

                                System.out.println(neighbor);
                                // Rotate the Block depending on its neighbors
                                switch (neighbor) {
                                    case(6): {
                                        break;
                                    }
                                    case (12):{
                                        blockState = blockState.setValue(FACING, facingSelf.getCounterClockWise());
                                        break;
                                    }
                                    case (9): {
                                        blockState = blockState.setValue(FACING, facingSelf.getOpposite());
                                        break;
                                    }
                                    case (3): {
                                        blockState = blockState.setValue(FACING, facingSelf.getClockWise());
                                        break;
                                    }
                                }
                                blockState = blockState.setValue(STATE, STATE_TOP_L);
                            }
                        }
                    }
                }
            }
        }
        return blockState;
    }

    /**
     * @param level
     * @param pos
     * @return 1 if neighbor is a roof_top type block, otherwise 0
     */
    private int getNeighbor(Level level, BlockPos pos, int state) {
        Block block = level.getBlockState(pos).getBlock();
        if(block.equals(ROOF_BLOCK))
            if(level.getBlockState(pos).getValue(STATE) >= state)
                return 1;
        return 0;
    }

    /**
     * @param level Level where the Block is
     * @param blockState BlockState for new Block
     * @param blockPos BlockPosition
     * @param state min. State that is valid
     * @return bitfield where a ROOF_BLOCK is a neighbor (Behind=8, CounterClock=4, Infront=2, Clockwise=1)
     */
    private int getNeighborIndex(Level level, BlockState blockState, BlockPos blockPos, int state){
        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());
        BlockPos blockPosInfront = blockPos.relative(blockState.getValue(FACING));
        BlockPos blockPosBehind = blockPos.relative(blockState.getValue(FACING).getOpposite());

        // set on bit for each Neighbor ROOF_BLOCK
        int neighbor =
                getNeighbor(level, blockPosSideClockwise, state) |
                        getNeighbor(level, blockPosInfront, state) << 1 |
                        getNeighbor(level, blockPosSideCounterClockwise, state) << 2 |
                        getNeighbor(level, blockPosBehind, state) << 3;
        return neighbor;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(FACING_ORG);
        builder.add(STATE);
        builder.add(MANUAL);
    }
}
