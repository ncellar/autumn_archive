var oldCaretPos = 0;

function childNodeIndex(child)
{
    return Array.prototype.indexOf.call(child.parentNode.childNodes, child);
}

function setCaret(position)
{
    oldCaretPos = position;

    document.getElementById("text").innerHTML
        = "<span id='eaten'>" + java.subText(0, position) + "</span>"
        + "<span id='caret'>|</span>"
        + java.subTextFrom(position);
}

function setLineCol(line, col)
{
    document.getElementById("line").value = line;
    document.getElementById("column").value = col;
}

function runClicked()
{
    var line = document.getElementById("line").value;
    var col = document.getElementById("column").value;
    setCaret(java.fileOffset(line, col));
}

function continueClicked()
{
    java.doContinue();
}

function stepClicked()
{
    java.doStep();
}

function textClicked()
{
    var sel = window.getSelection();
    var textIndex = sel.focusOffset;
    var nodeIndex = childNodeIndex(sel.focusNode);

    if (sel.focusNode.parentNode.id == "caret") { // click on caret
        var caretPos = oldCaretPos;
    }
    else if (nodeIndex == 0) { // click before caret
        var caretPos = textIndex;
    }
    else if (nodeIndex >= 1) { // click after caret
        // Don't worry about the case where the caret is at index 1, because the first alternative
        // of the if will be taken.
        var caretPos = oldCaretPos + textIndex;
    }

    setCaret(caretPos);
    setLineCol.apply(this, java.lineAndColumn(caretPos).split(","));
}

function pushFrame(exprName, line, column, offset)
{
    var li = document.createElement("li");
    li.innerHTML =
        "<div class='pos'>" + line + "<br/>" + column
        + "</div><div class='name' title='" + exprName + "'>" + exprName + "</div>";
    document.getElementById("stack").appendChild(li);
    setLineCol(line, column);
    setCaret(offset);
}

function popFrame()
{
    var stack = document.getElementById("stack");
    stack.removeChild(stack.lastChild);
}