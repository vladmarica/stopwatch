var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

function initializeCoreMod() {
  return {
    "stopwatch_fmlmodcontainer": {
      target: {
        type: "METHOD",
        "class": "net.minecraftforge.fml.javafmlmod.FMLModContainer",
        methodName: "acceptEvent",
        methodDesc: "(Lnet/minecraftforge/eventbus/api/Event;)V"
      },
      transformer: fmlModContainerTransformer
    }
  }
}

function fmlModContainerTransformer(methodNode) {
  methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), ASM.listOf(
    new VarInsnNode(Opcodes.ALOAD, 0),
    new VarInsnNode(Opcodes.ALOAD, 1),
    new MethodInsnNode(
      Opcodes.INVOKESTATIC,
      "com/vladmarica/stopwatch/coremod/CoreModHooks",
      "onModEventStart",
      "(Lnet/minecraftforge/fml/javafmlmod/FMLModContainer;Lnet/minecraftforge/eventbus/api/Event;)V")
  ));

  methodNode.instructions.insertBefore(
    ASM.findFirstInstruction(methodNode, Opcodes.RETURN), ASM.listOf(
      new VarInsnNode(Opcodes.ALOAD, 0),
      new VarInsnNode(Opcodes.ALOAD, 1),
      new MethodInsnNode(
        Opcodes.INVOKESTATIC,
        "com/vladmarica/stopwatch/coremod/CoreModHooks",
        "onModEventEnd",
        "(Lnet/minecraftforge/fml/javafmlmod/FMLModContainer;Lnet/minecraftforge/eventbus/api/Event;)V")));


  return methodNode;
}
