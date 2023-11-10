<template>
    <div class="pro-import">
        <div v-if="$slots.default" @click="open">
            <slot />
        </div>
        <el-button v-else type="primary" :disabled="disabled" plain @click="open">{{ btnText }}</el-button>
        <el-dialog
            :title="title"
            :append-to-body="true"
            :close-on-click-modal="false"
            v-model="dialogVisable"
            :before-close="handleClose"
            width="600px"
        >
            <div class="pro-import__inner">
                <div v-if="status === 1">
                    <slot name="form" />
                    <el-upload
                        ref="uploadRef"
                        class="upload-wrap"
                        drag
                        :limit="1"
                        :http-request="handleUpload"
                        :on-exceed="handleExceed"
                        :on-change="handleFileChange"
                        :action="action"
                        :auto-upload="false"
                    >
                        <i class="el-icon-upload" />
                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                        <template #tip>
                            支持扩展名：.xls .xlsx {{ uploadLimit | filesize }}k以内文件
                            <el-link type="primary" :href="tempUrl" target="_blank">下载模板</el-link>
                        </template>
                    </el-upload>
                    <el-checkbox v-model="cover" label="是否覆盖（慎点，耗时较长）"/>
                </div>
                <div v-if="status === 2" class="upload-progress">
                    <div class="upload-progress__inner">
                        <div class="file-name" :title="file.name">{{ file.name }}</div>
                        <el-progress :stroke-width="10" :percentage="percentage > 100 ? 100 : percentage" />
                    </div>
                </div>
                <div v-if="status === 3" class="upload-result">
                    <div class="upload-result__message">
                        <i :class="result.success ? 'el-icon-ext-success' : 'el-icon-ext-error-outline'" />
                        {{ result.message }}
                    </div>
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="handleClose">关 闭</el-button>
                    <el-button v-if="status === 1" :loading="loading" type="primary" @click="handleSubmit">确 定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { filesize } from "filesize"
import { isEmpty, cloneDeep } from "lodash-es"
import { ElUpload } from 'element-plus'
import { reactive, ref, toRefs } from 'vue'
import modal from "/@/utils/modal"
import { fileUploadExt } from "/@/api/common"

const uploadRef = ref<InstanceType<typeof ElUpload>>() 
    
const emits = defineEmits(["refresh","success","error"])

const data = reactive({
    loading: false,
    dialogVisable: false,
    status: 1,
    percentage: 0,
    file: {},
    fileNum: 0,
    timer: null,
    queryTimer: null,
    result: {
        success: false,
        message: ""
    },
    attachData: {},
    cover: false
})

const { attachData,status,file,fileNum,loading,timer,percentage,dialogVisable,result,queryTimer,cover } = toRefs(data)
    
const props = defineProps({
    disabled: {
        type: Boolean,
        default: false
    },
    title: {
        type: String,
        default: "导入"
    },
    btnText: {
        type: String,
        default: "导入"
    },
    uploadTips: {
        type: String,
        default: "将文件拖到此处，或<em>点击上传</em>"
    },
    uploadLimit: {
        type: Number,
        default: 10240
    },
    action: {
        type: String,
        default: "",
        required: true
    },
    tempUrl: {
        type: String,
        default: ""
    }, 
    data: {
        type: Object,
        default: () => ({})
    },
    prefixValid: {
        type: Function,
        default: () => null
    }
})

const handleSubmit = () => {
    if (props.prefixValid) {
        attachData.value = props.prefixValid()
    }
    if (status.value === 3) {
        return handleClose()
    } else if(fileNum.value==0){
        return modal.msgWarning("请选择文件")
    }
    loading.value = true
    uploadRef.value?.submit()
    status.value = 2
    easeProgress()
}

const handleUpload = ( uploadFile: any ) => {
    let f = new FormData()
    f.append('Filedata', uploadFile.file)
    f.append('cover', cover.value)
    fileUploadExt(f, {}, uploadFile.action, props.uploadLimit).then((res) => {
        if (res.code == 0) {
            importSuccess(res.msg)
        } else {
            importError(res.msg)
        }
    })
}
        
const handleExceed = () =>{
    modal.notifyWarning("已存在一个导入文件")
}


const importSuccess = (msg: string) => {
    loading.value = false
    clearTimeout(timer.value)
    percentage.value = 100
    emits('refresh')
    emits('success')
    setTimeout(() => {
        status.value =3
        result.value = {
            success: true,
            message: msg
        }
    }, 1000)   
           
}

const importError = (msg: string) => {
    loading.value = false
    emits('error')
    status.value =3
    result.value = {
        success: false,
        message: msg
    }
}

const handleClose =() => {
    if(queryTimer.value){
        clearTimeout(queryTimer.value)
    }    
    fileNum.value = 0
    attachData.value = {}
    status.value = 1
    percentage.value = 0
    uploadRef.value?.clearFiles()    
    emits('refresh')
    dialogVisable.value = false 
}

const handleFileChange = (file) => {
    const { name: fileName, size: fileSize } = file
    const ext = fileName.match(/^[^.]*\.{1}(.*)$/)
    if (!ext || !["xls", "xlsx"].includes(ext[1])) {
        uploadRef.value?.clearFiles()
        return modal.msgWarning("只能上传xlsx/xls格式文件")
    } else if (fileSize > props.uploadLimit * 1024) {
        uploadRef.value?.clearFiles()
        return modal.msgWarning(`文件大小不能超过${filesize(props.uploadLimit * 1024)}`)
    }
    fileNum.value = 1
}

const open = () => {
    if (props.disabled) {
        return false
    }
    fileNum.value = 0
    dialogVisable.value = true
}

const easeProgress =() => {
    if (percentage.value++ < 89) {
        timer.value = setTimeout(() => {
            easeProgress()
        }, 200 + percentage.value * 5)
    }
}
</script>
