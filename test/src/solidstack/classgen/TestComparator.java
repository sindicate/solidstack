/*
// javac version: 49.0

// Constant pool:
// 00001    MethodRef(Class("java/lang/Object"),NameAndType("()V", "<init>"))
// 00002    Fieldref(Class("solidstack/compiler/TestComparator"), NameAndType("Lsolidstack/script/objects/FunctionObject;", "function"))
// 00003    Class("java/lang/Object")
// 00004    MethodRef(Class("solidstack/script/objects/FunctionObject"),NameAndType("([Ljava/lang/Object;)Ljava/lang/Object;", "call"))
// 00005    Class("java/lang/Integer")
// 00006    MethodRef(Class("java/lang/Integer"),NameAndType("()I", "intValue"))
// 00007    Class("solidstack/compiler/TestComparator")
// 00008    Class("java/util/Comparator")
// 00009    "function"
// 00010    "Lsolidstack/script/objects/FunctionObject;"
// 00011    "<init>"
// 00012    "(Lsolidstack/script/objects/FunctionObject;)V"
// 00013    "Code"
// 00014    "LineNumberTable"
// 00015    "LocalVariableTable"
// 00016    "this"
// 00017    "Lsolidstack/compiler/TestComparator;"
// 00018    "compare"
// 00019    "(Ljava/lang/Object;Ljava/lang/Object;)I"
// 00020    "o1"
// 00021    "Ljava/lang/Object;"
// 00022    "o2"
// 00023    "SourceFile"
// 00024    "TestComparator.java"
// 00025    NameAndType("()V", "<init>")
// 00026    NameAndType("Lsolidstack/script/objects/FunctionObject;", "function")
// 00027    "java/lang/Object"
// 00028    Class("solidstack/script/objects/FunctionObject")
// 00029    NameAndType("([Ljava/lang/Object;)Ljava/lang/Object;", "call")
// 00030    "java/lang/Integer"
// 00031    NameAndType("()I", "intValue")
// 00032    "solidstack/compiler/TestComparator"
// 00033    "java/util/Comparator"
// 00034    "()V"
// 00035    "solidstack/script/objects/FunctionObject"
// 00036    "call"
// 00037    "([Ljava/lang/Object;)Ljava/lang/Object;"
// 00038    "intValue"
// 00039    "()I"

// Class attributes: SourceFile

package solidstack.compiler;
public class TestComparator implements java.util.Comparator
{
	// Method attributes: Code
	// Code attributes: LineNumberTable, LocalVariableTable

	// Line numbers:
	// 0 -> 106
	// 4 -> 107
	// 9 -> 108

	// Local variables:
	// 0: 0-10: this: Lsolidstack/compiler/TestComparator;
	// 1: 0-10: function: Lsolidstack/script/objects/FunctionObject;

	public TestComparator( solidstack.script.objects.FunctionObject function )
	{
		0:    aload           0                                                                               (1) 2A
		1:    invokespecial   MethodRef(Class("java/lang/Object"),NameAndType("()V", "<init>"))               (0) B7 00 01
		4:    aload           0                                                                               (1) 2A
		5:    aload           1                                                                               (2) 2B
		6:    putfield        Fieldref(Class("solidstack/compiler/TestComparator"), NameAndType("Lsolidstack/script/objects/FunctionObject;", "function"))  (0) B5 00 02
		9:    return                                                                                          (0) B1

		// Pieces: 1
		// piece: 0 - 10 next: -1

		     0


//   =M= MethodDecompiler starting block, depth = [1], pc = [0]
		//	0:    aload           0                                                                               (1) 2A
		//	1:    invokespecial   MethodRef(Class("java/lang/Object"),NameAndType("()V", "<init>"))               (0) B7 00 01
		super()
		//	4:    aload           0                                                                               (1) 2A
		//	5:    aload           1                                                                               (2) 2B
		//	6:    putfield        Fieldref(Class("solidstack/compiler/TestComparator"), NameAndType("Lsolidstack/script/objects/FunctionObject;", "function"))  (0) B5 00 02
		this.function = function
		//	9:    return                                                                                          (0) B1
		return
//   =M= MethodDecompiler found execution end
	}

	// Method attributes: Code
	// Code attributes: LineNumberTable, LocalVariableTable

	// Line numbers:
	// 0 -> 113

	// Local variables:
	// 0: 0-26: this: Lsolidstack/compiler/TestComparator;
	// 1: 0-26: o1: Ljava/lang/Object;
	// 2: 0-26: o2: Ljava/lang/Object;

	public int compare( java.lang.Object o1, java.lang.Object o2 )
	{
		0:    aload           0                                                                               (1) 2A
		1:    getfield        Fieldref(Class("solidstack/compiler/TestComparator"), NameAndType("Lsolidstack/script/objects/FunctionObject;", "function"))  (1) B4 00 02
		4:    iconst_2                                                                                        (2) 05
		5:    anewarray       Class("java/lang/Object")                                                       (2) BD 00 03
		8:    dup                                                                                             (3) 59
		9:    iconst_0                                                                                        (4) 03
		10:   aload           1                                                                               (5) 2B
		11:   aastore                                                                                         (2) 53
		12:   dup                                                                                             (3) 59
		13:   iconst_1                                                                                        (4) 04
		14:   aload           2                                                                               (5) 2C
		15:   aastore                                                                                         (2) 53
		16:   invokevirtual   MethodRef(Class("solidstack/script/objects/FunctionObject"),NameAndType("([Ljava/lang/Object;)Ljava/lang/Object;", "call"))  (1) B6 00 04
		19:   checkcast       Class("java/lang/Integer")                                                      (1) C0 00 05
		22:   invokevirtual   MethodRef(Class("java/lang/Integer"),NameAndType("()I", "intValue"))            (1) B6 00 06
		25:   ireturn                                                                                         (0) AC

		// Pieces: 1
		// piece: 0 - 26 next: -1

		     0


//   =M= MethodDecompiler starting block, depth = [1], pc = [0]
		//	0:    aload           0                                                                               (1) 2A
		//	1:    getfield        Fieldref(Class("solidstack/compiler/TestComparator"), NameAndType("Lsolidstack/script/objects/FunctionObject;", "function"))  (1) B4 00 02
		//	4:    iconst_2                                                                                        (2) 05
		//	5:    anewarray       Class("java/lang/Object")                                                       (2) BD 00 03
		//	8:    dup                                                                                             (3) 59
		//	9:    iconst_0                                                                                        (4) 03
		//	10:   aload           1                                                                               (5) 2B
		//	11:   aastore                                                                                         (2) 53
		//	12:   dup                                                                                             (3) 59
		//	13:   iconst_1                                                                                        (4) 04
		//	14:   aload           2                                                                               (5) 2C
		//	15:   aastore                                                                                         (2) 53
		//	16:   invokevirtual   MethodRef(Class("solidstack/script/objects/FunctionObject"),NameAndType("([Ljava/lang/Object;)Ljava/lang/Object;", "call"))  (1) B6 00 04
		//	19:   checkcast       Class("java/lang/Integer")                                                      (1) C0 00 05
		//	22:   invokevirtual   MethodRef(Class("java/lang/Integer"),NameAndType("()I", "intValue"))            (1) B6 00 06
		//	25:   ireturn                                                                                         (0) AC
		return ( (java.lang.Integer)this.function.call( new java.lang.Object[] { o1, o2 } ) ).intValue()
//   =M= MethodDecompiler found execution end
	}

}

*/

package solidstack.classgen;

import java.util.Comparator;

import solidstack.script.objects.FunctionObject;

public class TestComparator implements Comparator
{
	private FunctionObject function;

	public TestComparator( FunctionObject function )
	{
		this.function = function;
	}

	@Override
	public int compare( Object o1, Object o2 )
	{
		return (Integer)this.function.call( o1, o2 );
	}
}
