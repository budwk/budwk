<template>
    <div class="user-info-head" @click="editCropper()">
    <img v-if="props.modelValue" :src="platformInfo.AppFileDomain + props.modelValue" title="点击上传头像"
            class="img-circle img-lg" />
    <img v-else src="" class="img-circle img-lg"/>    
    </div>
    <el-dialog :title="title" v-model="open" width="800px" append-to-body @opened="modalOpened" @close="closeDialog">
        <el-row>
            <el-col :xs="24" :md="12" :style="{ height: '350px' }">
                <vue-cropper ref="cropperRef" :img="options.img" :info="true" :autoCrop="options.autoCrop"
                    :autoCropWidth="options.autoCropWidth" :autoCropHeight="options.autoCropHeight"
                    :fixedBox="options.fixedBox" @realTime="realTime" v-if="visible" />
            </el-col>
            <el-col :xs="24" :md="12" :style="{ height: '350px' }">
                <div class="avatar-upload-preview">
                    <img :src="options.previews.url" :style="options.previews.img" />
                </div>
            </el-col>
        </el-row>
        <br />
        <el-row>
            <el-col :lg="2" :md="2">
                <el-upload action="#" :http-request="requestUpload" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <el-button>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-col>
            <el-col :lg="{ span: 1, offset: 2 }" :md="2">
                <el-button icon="Plus" @click="changeScale(1)"></el-button>
            </el-col>
            <el-col :lg="{ span: 1, offset: 1 }" :md="2">
                <el-button icon="Minus" @click="changeScale(-1)"></el-button>
            </el-col>
            <el-col :lg="{ span: 1, offset: 1 }" :md="2">
                <el-button icon="RefreshLeft" @click="rotateLeft()"></el-button>
            </el-col>
            <el-col :lg="{ span: 1, offset: 1 }" :md="2">
                <el-button icon="RefreshRight" @click="rotateRight()"></el-button>
            </el-col>
            <el-col :lg="{ span: 2, offset: 6 }" :md="2">
                <el-button type="primary" @click="uploadImg()">提 交</el-button>
            </el-col>
        </el-row>
    </el-dialog>
</template>
  
<script setup lang="ts">
import 'vue-cropper/dist/index.css'
import { VueCropper } from 'vue-cropper'
import { onMounted, reactive, ref } from "vue"
import modal from "/@/utils/modal"
import { fileUpload } from '/@/api/common'
import { usePlatformInfo } from "/@/stores/platformInfo"
import { useUserInfo } from '/@/stores/userInfo'
import { doChangeAvatar } from '/@/api/platform/home/user'

const platformInfo = usePlatformInfo()
const userInfo = useUserInfo()

const cropperRef = ref<InstanceType<typeof VueCropper>>() 
const open = ref(false)
const visible = ref(false)
const title = ref("修改头像")

const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
})

const emits = defineEmits(['update:modelValue'])

//图片裁剪数据
const options = reactive({
    img:  '', // 裁剪图片的地址
    autoCrop: true, // 是否默认生成截图框
    autoCropWidth: 200, // 默认生成截图框宽度
    autoCropHeight: 200, // 默认生成截图框高度
    fixedBox: true, // 固定截图框大小 不允许改变
    previews: {} //预览数据
})

/** 编辑头像 */
const editCropper = () => {
    open.value = true
}
/** 打开弹出层结束时的回调 */
const modalOpened = () => {
    visible.value = true
}
/** 覆盖默认上传行为 */
const requestUpload = () => {
}
/** 向左旋转 */
const rotateLeft = () => {
    cropperRef.value.rotateLeft()
}
/** 向右旋转 */
const rotateRight = () => {
    cropperRef.value.rotateRight()
}
/** 图片缩放 */
const changeScale = (num: number) => {
    num = num || 1
    cropperRef.value.changeScale(num)
}
/** 上传预处理 */
const beforeUpload = (file: any) => {
    if (file.type.indexOf("image/") == -1) {
        modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。")
    } else {
        const reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = () => {
            options.img = reader.result as string
        }
    }
}
/** 上传图片 */
const uploadImg = () => {
    cropperRef.value.getCropBlob((data: any) => {
        let formData = new FormData()
        formData.append('Filedata', data)
        fileUpload(formData,{},'blob').then((res) => {
            if (res.code == 0) {
                doChangeAvatar(res.data.url).then((r)=>{
                    open.value = false
                    options.img = res.data.url
                    userInfo.user.avatar = res.data.url
                    emits('update:modelValue', res.data.url)
                    modal.msgSuccess('修改成功')
                    visible.value = false
                })
            }
        })
    });
};
/** 实时预览 */
const realTime = (data: any) => {
    options.previews = data
}
/** 关闭窗口 */
const closeDialog = () => {
    options.img = platformInfo.AppFileDomain + props.modelValue
    visible.value = false
}

onMounted(()=>{
    setTimeout(()=>{
        options.img = platformInfo.AppFileDomain + props.modelValue
    },200)
})
</script>
  
<style lang='scss' scoped>
.user-info-head {
    position: relative;
    display: inline-block;
    height: 120px;
}

.user-info-head:hover:after {
    content: "+";
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    color: #eee;
    background: rgba(0, 0, 0, 0.5);
    font-size: 24px;
    font-style: normal;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    cursor: pointer;
    line-height: 110px;
    border-radius: 50%;
}
</style>