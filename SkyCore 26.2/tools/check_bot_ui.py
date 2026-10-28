"""Static regression checks. Requires: pip install tree-sitter tree-sitter-java.
This is NOT a javac compilation or an in-game test.
Run from any directory: python tools/check_bot_ui.py
"""
from pathlib import Path
import unittest
from tree_sitter import Language, Parser
import tree_sitter_java

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / 'src/main/java/net/skycore/client'
PARSER = Parser(Language(tree_sitter_java.language()))


def nodes(node):
    yield node
    for child in node.children:
        yield from nodes(child)


class BotUiChecks(unittest.TestCase):
    def test_java_syntax(self):
        files = list((JAVA / 'bot').glob('*.java')) + [
            JAVA / 'gui/workspace/WorkspaceBotPanel.java',
            JAVA / 'gui/workspace/WorkspaceScreen.java', JAVA / 'SkycoreClient.java',
            JAVA / 'gui/workspace/SkycoreBotPage.java', JAVA / 'util/RenderGuiHelper.java']
        for path in files:
            with self.subTest(file=path.name):
                self.assertFalse(PARSER.parse(path.read_bytes()).root_node.has_error)

    def test_internal_panel_call_arities(self):
        path = JAVA / 'gui/workspace/WorkspaceBotPanel.java'
        source = path.read_bytes()
        tree = PARSER.parse(source)
        text = lambda node: source[node.start_byte:node.end_byte].decode()
        declarations = {}
        for node in nodes(tree.root_node):
            if node.type == 'method_declaration':
                declarations[text(node.child_by_field_name('name'))] = len(
                    node.child_by_field_name('parameters').named_children)
        checked = 0
        for node in nodes(tree.root_node):
            if node.type == 'method_invocation' and node.child_by_field_name('object') is None:
                name = text(node.child_by_field_name('name'))
                if name in declarations:
                    self.assertEqual(declarations[name], len(node.child_by_field_name('arguments').named_children), name)
                    checked += 1
        self.assertGreater(checked, 20)

    def test_vote_loading_uses_cell_normalization(self):
        source = (JAVA / 'bot/BotConfig.java').read_text(encoding='utf-8')
        for key in ('elytraCell', 'pvpCell', 'resourcesCell'):
            self.assertIn(f'{key} = normalizeCell((int) number(json, "{key}"', source)

    def test_script_server_and_slots(self):
        source = (JAVA / 'bot/BotScript.java').read_text(encoding='utf-8')
        for snippet in ('SERVER = "mc.musteryworld.net"', 'SERVER_CELL_FIRST = 8',
                        'SERVER_CELL_SECOND = 11', 'CATEGORY_CELL_FIRST = 11',
                        'CATEGORY_CELL_SECOND = 10', 'VOTE_CELL_A = 12', 'VOTE_CELL_B = 14'):
            self.assertIn(snippet, source)

    def test_password_not_in_controller_log(self):
        source = (JAVA / 'bot/BedWarsBot.java').read_text(encoding='utf-8')
        self.assertNotIn('BotConfig.registerCommand()', source)
        self.assertIn('/reg ********', source)

    def test_skycore_navigation_and_event_routing(self):
        source = (JAVA / 'util/RenderGuiHelper.java').read_text(encoding='utf-8')
        self.assertIn('BOT("Bot",', source)
        self.assertIn('viewValues[8] = BOT;', source)
        for call in ('render', 'click', 'key', 'type', 'scroll', 'drag', 'release', 'leave'):
            self.assertIn('this.botPage.' + call + '(', source)
        self.assertIn('this.botMouseX = coordinate;', source)
        self.assertIn('this.botMouseY = coordinate2;', source)

    def test_skycore_uses_receiving_palette_and_shared_renderer(self):
        source = (JAVA / 'gui/workspace/SkycoreBotPage.java').read_text(encoding='utf-8')
        for token in ('host.surfaceColor()', 'host.primaryColor()', 'host.accentColor()',
                      'host.fieldColor()', 'panel.render(', 'panel.notification('):
            self.assertIn(token, source)
        self.assertNotIn('new BedWarsBot', source)
        self.assertNotIn('setScreen', source)

    def test_timeline_breath_progress_and_first_step(self):
        source = (JAVA / 'gui/workspace/WorkspaceBotPanel.java').read_text(encoding='utf-8')
        self.assertIn('STAGE_TEXT[i] = Integer.toString(i);', source)
        self.assertIn('Math.sin(', source)
        self.assertIn('System.nanoTime()', source)
        self.assertIn('MenuPrefs.animations', source)
        self.assertIn('anim.to(KEY_BASE + 401', source)
        self.assertIn('progress * rowHeight', source)

    def test_full_vote_answers_and_module_font(self):
        source = (JAVA / 'gui/workspace/WorkspaceBotPanel.java').read_text(encoding='utf-8')
        self.assertIn('vote.optionLabel(BotScript.VOTE_CELL_A)', source)
        self.assertIn('vote.optionLabel(BotScript.VOTE_CELL_B)', source)
        self.assertNotIn('segmentHeight, BotScript.CELL_', source)
        self.assertNotIn('Weight.BOLD', source)
        self.assertNotIn('Weight.SEMIBOLD', source)
        data = (JAVA / 'bot/BotScript.java').read_text(encoding='utf-8')
        self.assertIn('"Виртуальный", "Virtual", "Классика", "Classic"', data)

    def test_team_colors_require_menu_snapshot(self):
        source = (JAVA / 'gui/workspace/WorkspaceBotPanel.java').read_text(encoding='utf-8')
        self.assertIn('int count = bot.detectedTeamColorCount();', source)
        self.assertNotIn('int[] colors = BotScript.WOOL_COLORS;', source)
        model = (JAVA / 'bot/BedWarsBot.java').read_text(encoding='utf-8')
        self.assertIn('if (!running || stage != BotStage.TEAM_COLORS) return;', model)
        self.assertIn('"Выбор команды".equals(title)', model)
        self.assertIn('index >= 0 && index < 16 && !seen[index]', model)
        self.assertIn('return teamColors.clone();', model)

    def test_wrapper(self):
        import zipfile
        with zipfile.ZipFile(ROOT / 'gradle/wrapper/gradle-wrapper.jar') as jar:
            self.assertIsNone(jar.testzip())
            self.assertIn('org/gradle/wrapper/GradleWrapperMain.class', jar.namelist())
        props = (ROOT / 'gradle/wrapper/gradle-wrapper.properties').read_text()
        self.assertIn('distributionSha256Sum=', props)


if __name__ == '__main__':
    unittest.main(verbosity=2)
