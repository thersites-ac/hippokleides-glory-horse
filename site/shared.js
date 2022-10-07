const navs = document.querySelector('.nav-links')
const navItems = document.querySelectorAll('.nav-item')
const navLinksDropdown = 'nav-links-dropdown'

const setDismiss = (element) => element.onclick = () => {
    navs.classList.remove(navLinksDropdown)
}

setDismiss(document.querySelector('main'))
setDismiss(document.querySelector('footer'))

document.querySelector('.nav-hamburger').onclick = () => {
    navs.classList.toggle(navLinksDropdown)
}

