var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

function initializeCoreMod() {
  return {
    "stopwatch_clientmodloader": {
      target: {
        type: "METHOD",
        "class": "net.minecraftforge.fml.client.ClientModLoader",
        methodName: "finishModLoading",
        methodDesc: "(Lnet/minecraftforge/fml/ModWorkManager$DrivenExecutor;Ljava/util/concurrent/Executor;)V"
      },
      transformer: clientModLoaderTransformer
    }
  }
}

function clientModLoaderTransformer(methodNode) {
  methodNode.instructions.insertBefore(
    ASM.findFirstInstruction(methodNode, Opcodes.RETURN),
    new MethodInsnNode(
      Opcodes.INVOKESTATIC,
      "com/vladmarica/stopwatch/coremod/CoreModHooks",
      "onLoadingComplete",
      "()V"));

  return methodNode;
}
