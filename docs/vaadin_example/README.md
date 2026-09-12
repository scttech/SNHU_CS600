# Vaadin Component Showcase

A UI-only reference project: a multi-view catalog of Vaadin components and layouts, built for
developers who want to see how a component looks and behaves before wiring it into their own
screens. 

There is no database, no REST API, and no Spring Security here — every example runs entirely in
memory. TLS is still enabled, the same as module3/module5 (see
[docs/module3/rest/README.md#tls-and-basic-auth](../module3/rest/README.md)), using a self-signed,
dev-only certificate committed at
[`src/main/resources/keystore.p12`](../../vaadin_example/src/main/resources/keystore.p12). This
isn't optional here: Vaadin's server-driven UI relies on a session cookie to track each browser's
UI state, and Safari in particular refuses to persist that cookie for a plain HTTP site — the app
would otherwise fail to run in Safari.

## Running the code

From within the `vaadin_example` directory, run:

```shell
../mvnw spring-boot:run
```

Then browse to [https://localhost:8090](https://localhost:8090). Your browser will warn about the
self-signed certificate the first time — accept/proceed past that warning (Safari: "Show Details" →
"visit this website"; for full trust, including a working session cookie in Safari, import
`keystore.p12`'s certificate into macOS Keychain and mark it trusted).

## What's here

Each view under
[`src/main/java/.../ui`](../../vaadin_example/src/main/java/com/scttech/vaadinexample/ui) is a
category of components (buttons, text inputs, selection, date/time, data display, layouts,
feedback, forms). Every example on a view is built from a shared
[`Showcase`](../../vaadin_example/src/main/java/com/scttech/vaadinexample/ui/component/Showcase.java)
component that renders:

* the live component(s),
* a collapsible "View source" panel with the exact code that built the demo, and
* a "Copy code" button that copies that snippet to the clipboard.

To add a new example, add a `Showcase("Title", "optional description", demoComponent, code)` call
to an existing view (or a new `@Route(..., layout = MainLayout.class)` view, registered in
[`MainLayout`](../../vaadin_example/src/main/java/com/scttech/vaadinexample/ui/MainLayout.java)'s
side nav) — the source string passed in should match the demo code exactly, since it's what a
developer will copy-paste.

## Additional Resources

* [Vaadin Showcase](https://vaadin.com/showcase)
* [Vaadin Examples and Demos](https://vaadin.com/examples-and-demos)
