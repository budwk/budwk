export default function({ store, redirect, next }) {
  if (store.state.auth) {
    return redirect(process.env.PLATFORM_PATH)
  }
}
