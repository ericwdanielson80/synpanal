package neuron_analyzer;

import java.io.DataOutputStream;

public class SaveIndentifier {

	static final int ThresholdRedStart_v1 = 0;
	static final int ThresholdRedEnd_v1 = 1;
	static final int ThresholdGreenStart_v1 = 2;
	static final int ThresholdGreenEnd_v1 = 3;
	static final int ThresholdBlueStart_v1 = 4;
	static final int ThresholdBlueEnd_v1 = 5;
	static final int DendriteSimple_v1 = 6;
	static final int DendriteComplex_v1 = 7;
	static final int Puncta_v1 = 8;
	static final int Spine_v1 = 9;
	static final int CellBody_v1 = 10;
	static final int DendriteWidth_v1 = 11;
	static final int DendriteArea_v1 = 12;
	static final int SpineHead_v1 = 13;
	static final int SpineNeck_v1 = 14;
	static final int SpineLength_v1 = 15;
	static final int PunctaPerSpine_v1 = 16;
	static final int DendriteVertex_v1 = 17;
	static final int DendriteWatchColor_v1 = 18;
	static final int DendriteGroupMember_v1 = 19;
	
	static public void saveDendrite(int version,DataOutputStream ds, IoContainer i)
	{
		switch(version)
		{
		case DendriteSimple_v1: saveDendriteSimplev1(ds, i); break;
		default:
		{
			System.out.println("Dendrite Version not found");
		}
		}
	}
	
	static public int getLatestSimpleDendriteVersion()
	{
		return DendriteSimple_v1;
	}
	
	static public int getLatestDendriteVertexVersion()
	{
		return DendriteVertex_v1;
	}
	
	static public int getLatestDendriteWatchColorVersion()
	{
		return DendriteVertex_v1;
	}
	
	static public int getLatestDendriteGroupMemberVersion()
	{
		return DendriteGroupMember_v1;
	}
	
	private static void saveDendriteSimplev1(DataOutputStream ds, IoContainer i)
	{
		//saveType(ds,i);		
    	//saveVertex(ds,i);
    	//saveWatchColor(ds,i);
    	//saveGroupMember(ds,i);
    	//saveSpineInfo(ds,i);
    	//savePunctaInfo(ds,i);
	}
	
	private static void saveDendriteType(DataOutputStream ds, IoContainer i)
	{
		i.writeInt(ds, "Dendrite Type", getLatestSimpleDendriteVersion());
	}
	
	private static void saveDendriteVertex(DataOutputStream ds, IoContainer i)
	{
		i.writeInt(ds, "Dendrite Vertex", getLatestDendriteVertexVersion());
	}
	
	private static void saveDendriteWatchColor(DataOutputStream ds, IoContainer i)
	{
		i.writeInt(ds, "Dendrite Watch Color", getLatestDendriteWatchColorVersion());
	}
	
	private static void saveDendriteGroupMember(DataOutputStream ds, IoContainer i)
	{
		i.writeInt(ds, "Dendrite Group Member", getLatestDendriteGroupMemberVersion());
	}
	
	private static void saveSpineInfo(DataOutputStream ds, IoContainer i)
	{
		
	}
	
	private static void savePunctaInfo(DataOutputStream ds, IoContainer i)
	{
		
	}
	
}
