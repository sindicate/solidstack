/*

// javac version: 49.0

// Constant pool:
// 00001    MethodRef(Class("java/lang/Object"),NameAndType("()V", "<init>"))
// 00002    Class("solidstack/compiler/TestComparator")
// 00003    Class("java/lang/Object")
// 00004    Class("java/util/Comparator")
// 00005    "<init>"
// 00006    "()V"
// 00007    "Code"
// 00008    "LineNumberTable"
// 00009    "LocalVariableTable"
// 00010    "this"
// 00011    "Lsolidstack/compiler/TestComparator;"
// 00012    "compare"
// 00013    "(Ljava/lang/Object;Ljava/lang/Object;)I"
// 00014    "o1"
// 00015    "Ljava/lang/Object;"
// 00016    "o2"
// 00017    "SourceFile"
// 00018    "TestComparator.java"
// 00019    NameAndType("()V", "<init>")
// 00020    "solidstack/compiler/TestComparator"
// 00021    "java/lang/Object"
// 00022    "java/util/Comparator"

// Class attributes: SourceFile

package solidstack.compiler;
public class TestComparator implements java.util.Comparator
{
	// Method attributes: Code
	// Code attributes: LineNumberTable, LocalVariableTable

	// Line numbers:
	// 0 -> 5

	// Local variables:
	// 0: 0-5: this: Lsolidstack/compiler/TestComparator;

	public TestComparator()
	{
		0:    aload           0                                                                               (1) 2A
		1:    invokespecial   MethodRef(Class("java/lang/Object"),NameAndType("()V", "<init>"))               (0) B7 00 01
		4:    return                                                                                          (0) B1

		// Pieces: 1
		// piece: 0 - 5 next: -1

		     0

//   =M= MethodDecompiler starting block, depth = [1], pc = [0]
		//	0:    aload           0                                                                               (1) 2A
		//	1:    invokespecial   MethodRef(Class("java/lang/Object"),NameAndType("()V", "<init>"))               (0) B7 00 01
		super()
		//	4:    return                                                                                          (0) B1
		return
//   =M= MethodDecompiler found execution end
	}

	// Method attributes: Code
	// Code attributes: LineNumberTable, LocalVariableTable

	// Line numbers:
	// 0 -> 10

	// Local variables:
	// 0: 0-2: this: Lsolidstack/compiler/TestComparator;
	// 1: 0-2: o1: Ljava/lang/Object;
	// 2: 0-2: o2: Ljava/lang/Object;

	public int compare( java.lang.Object o1, java.lang.Object o2 )
	{
		0:    iconst_0                                                                                        (1) 03
		1:    ireturn                                                                                         (0) AC

		// Pieces: 1
		// piece: 0 - 2 next: -1

		     0

//   =M= MethodDecompiler starting block, depth = [1], pc = [0]
		//	0:    iconst_0                                                                                        (1) 03
		//	1:    ireturn                                                                                         (0) AC
		return 0
//   =M= MethodDecompiler found execution end
	}

}

*/

package solidstack.compiler;

import java.util.Comparator;

public class TestComparator implements Comparator
{
	@Override
	public int compare( Object o1, Object o2 )
	{
		return 0;
	}
}
