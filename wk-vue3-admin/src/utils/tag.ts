import router from '../router/index';
import { useTagsView } from '/@/stores/tagsView'

export default {
    // 刷新当前tab页签
    refreshPage(obj: any) {
        const { path, query, matched } = router.currentRoute.value
        if (obj === undefined) {
            return
        }
        return useTagsView().delCachedView(obj).then(() => {
            const { path, query } = obj
            router.replace({
                path: '/redirect' + path,
                query: query
            })
        })
    },
    // 关闭当前tab页签，打开新页签
    closeOpenPage(obj: any) {
        useTagsView().delView(router.currentRoute.value);
        if (obj !== undefined) {
            return router.push(obj);
        }
    },
    // 关闭指定tab页签
    closePage(obj = undefined) {
        if (obj === undefined) {
            return useTagsView().delView(router.currentRoute.value).then(({ lastPath }) => {
                return router.push(lastPath || '/platform/dashboard');
            });
        }
        return useTagsView().delView(obj);
    },
    // 关闭所有tab页签
    closeAllPage() {
        return useTagsView().delAllViews()
    },
    // 关闭左侧tab页签
    closeLeftPage(obj: any) {
        return useTagsView().delLeftTags(obj || router.currentRoute.value);
    },
    // 关闭右侧tab页签
    closeRightPage(obj: any) {
        return useTagsView().delRightTags(obj || router.currentRoute.value);
    },
    // 关闭其他tab页签
    closeOtherPage(obj: any) {
        return useTagsView().delOthersViews(obj || router.currentRoute.value);
    },
    // 打开tab页签
    openPage(url: string) {
        return router.push(url);
    },
    // 修改tab页签
    updatePage(obj: any) {
        return useTagsView().updateVisitedView(obj);
    }
}