<template>
  <div>
    <quill-editor
      id='editor'
      v-model="content"
      ref="myQuillEditor"
      :options="editorOption"
      @change="onEditorChange($event)"
    >
    </quill-editor>

  </div>
</template>
<script>
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
import { quillEditor } from "vue-quill-editor";
import { Quill } from "vue-quill-editor";
import { ImageDrop } from "quill-image-drop-module";
import { ImageResize } from "quill-image-resize-module";
Quill.register("modules/imageDrop", ImageDrop);
Quill.register("modules/imageResize", ImageResize);

export default {
  name: "Editor",

  data() {
    return {
      content: null,
      editorOption: {
        placeholder: "输入任何内容，支持html",
        modules: {
          toolbar: [
            [{ size: ["small", false, "large"] }],
            ["bold", "italic"],
            [{ list: "ordered" }, { list: "bullet" }],
            ["link", "image"]
          ],
          history: {
            delay: 1000,
            maxStack: 50,
            userOnly: false
          },
          imageDrop: true,
          imageResize: {
            displayStyles: {
              backgroundColor: "black",
              border: "none",
              color: "white"
            },
            modules: ["Resize", "DisplaySize", "Toolbar"]
          }
        }
      }
    };
  },
  components: { "quill-editor": quillEditor },
  created() {},
  mounted() {},
  methods: {
 
    onEditorChange() {
      //内容改变事件
      this.$emit('content',this.content)
    }
  }
};
</script>
<style scoped>
#editor >>> .ql-editor{
  min-height: 300px;
}
</style>