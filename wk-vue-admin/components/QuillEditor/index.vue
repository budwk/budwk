<template>
    <section class="container ql-snow">
        <el-upload
            class="upload-demo"
            name="Filedata"
            :action="uploadurl"
            accept=".jpg,.jpeg,.png,.gif]"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleSuccess"
            :before-upload="beforeUpload"
            id="uploadImg"
            ref="uploadImg"
        >
        </el-upload>
        <div
            ref="myQuillEditor"
            v-quill:myQuillEditor="editorOption"
            class="quill-editor"
            :content="content"
            @change="onEditorChange($event)"
            @blur="onEditorBlur($event)"
            @focus="onEditorFocus($event)"
            @ready="onEditorReady($event)"
        />
    </section>
</template>

<script>
import "quill/dist/quill.snow.css"
import "quill/dist/quill.bubble.css"
import "quill/dist/quill.core.css"

const Cookie = require("js-cookie")
export default {
    name: "QuillEditor",
    props: ["uploadurl", "filedomain", "text"],
    data() {
        const self = this
        return {
            content: this.text,
            headers: {
                "wk-user-token": Cookie.get("wk-user-token")
            },
            editorOption: {
                // some quill options
                modules: {
                    toolbar: {
                        container: [
                            ["bold", "italic", "underline", "strike"], // 切换按钮
                            ["blockquote", "code-block"],
                            // [{ header: 1 }, { header: 2 }], // 用户自定义按钮值
                            [{ list: "ordered" }, { list: "bullet" }],
                            [{ script: "sub" }, { script: "super" }], // 上标/下标
                            [{ indent: "-1" }, { indent: "+1" }], // 减少缩进/缩进
                            [{ direction: "rtl" }], // 文本下划线

                            [{ size: ["small", false, "large", "huge"] }], // 用户自定义下拉
                            [{ header: [1, 2, 3, 4, 5, 6, false] }],

                            [{ color: [] }, { background: [] }], // 主题默认下拉，使用主题提供的值
                            [{ align: [] }],
                            ["link", "image", "video"], //上传图片
                            ["clean"] // 清除格式
                        ],
                        handlers: {
                            image(value) {
                                if (value) {
                                    self.imgHandler(this)
                                } else {
                                    this.quill.format("image", false)
                                }
                            }
                        }
                    }
                },
                placeholder: "",
                theme: "snow",
                quill: ""
            }
        }
    },
    mounted() {
        
    },
    methods: {
        onEditorBlur(editor) {
            // console.log("editor blur!", editor)
        },
        onEditorFocus(editor) {
            // console.log("editor focus!", editor)
        },
        onEditorReady(editor) {
            // console.log("editor ready!", editor)
        },
        onEditorChange({ editor, html, text }) {
            this.content = html
            this.$emit("on-change", html)
        },
        imgHandler(handle) {
            this.quill = handle.quill
            document.querySelector("#uploadImg .el-upload").click()
        },
        beforeUpload() {
            this.headers["wk-user-token"] = Cookie.get("wk-user-token")
        },
        // 图片上传成功方法
        handleSuccess(response, file, fileList) {
            // 如果上传成功
            if (response) {
                if (response.code == 0) {
                    // 获取光标所在位置
                    let length = this.quill.getSelection().index
                    // 插入图片，res为服务器返回的图片链接地址
                    this.quill.insertEmbed(
                        length,
                        "image",
                        this.filedomain + response.data.url
                    )
                    // 调整光标到最后
                    this.quill.setSelection(length + 1)
                } else {
                    this.$message.error(response.msg)
                }
            } else {
                this.$message.error("上传出错..")
            }
        }
    }
}
</script>

<style scoped>
.upload-demo {
    width: 0px;
    height: 0px;
}
.container {
    width: 100%;
    margin: 0 auto;
    /* padding: 10px 0; */
}
.quill-editor {
    min-height: 300px;
    max-height: 600px;
    overflow-y: auto;
}
::v-deep .ql-snow .ql-picker {
    height: auto;
    z-index: 2099;
}
</style>
